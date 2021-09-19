package id.thork.app.pages.labor_plan.element

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.response.work_order.Member
import id.thork.app.persistence.entity.*
import id.thork.app.repository.LaborRepository
import id.thork.app.repository.TaskRepository
import id.thork.app.repository.WorkOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 23/06/21
 * Jakarta, Indonesia.
 */
class LaborPlanViewModel @ViewModelInject constructor(
    private val laborRepository: LaborRepository,
    private val workOrderRepository: WorkOrderRepository,
    private val taskRepository: TaskRepository,
    private val preferenceManager: PreferenceManager,

    ) : LiveCoroutinesViewModel() {
    val TAG = LaborPlanViewModel::class.java.name

    private val _getLaborPlanList = MutableLiveData<List<LaborPlanEntity>>()
    val getLaborPlanList: LiveData<List<LaborPlanEntity>> get() = _getLaborPlanList

    private val _laborPlanCache = MutableLiveData<LaborPlanEntity>()
    val laborPlanCache: LiveData<LaborPlanEntity> get() = _laborPlanCache

    private val _woCache = MutableLiveData<WoCacheEntity>()
    val woCache: LiveData<WoCacheEntity> get() = _woCache

    private val _getLaborMaster = MutableLiveData<List<LaborMasterEntity>>()
    val getLaborMaster: LiveData<List<LaborMasterEntity>> get() = _getLaborMaster

    private val _getCraftMasterEntity = MutableLiveData<List<CraftMasterEntity>>()
    val getCraftMasterEntity: LiveData<List<CraftMasterEntity>> get() = _getCraftMasterEntity

    private val _getCraftMaster = MutableLiveData<List<CraftMasterEntity>>()
    val getCraftMaster: LiveData<List<CraftMasterEntity>> get() = _getCraftMaster

    private val _taskList = MutableLiveData<List<TaskEntity>>()
    val taskList: LiveData<List<TaskEntity>> get() = _taskList

    private val _craftEntity = MutableLiveData<CraftMasterEntity>()
    val craftEntity: LiveData<CraftMasterEntity> get() = _craftEntity

    private val _listCraft = MutableLiveData<List<String>>()
    val listCraft: LiveData<List<String>> get() = _listCraft

    fun fetchListLaborPlan(workroderid: String) {
        val listLabor = laborRepository.findListLaborplanWorkorderid(workroderid)
        listLabor.whatIfNotNullOrEmpty {
            _getLaborPlanList.value = it
        }
    }

    fun fetchLaborPlanByObjectboxid(objectboxid : Long) {
        val laborCache = laborRepository.findlaborPlanByObjectboxId(objectboxid)
        laborCache.whatIfNotNull {
            _laborPlanCache.value = it
        }
    }

    fun fetchWoCache(wonum: String) {
        val parent = workOrderRepository.findWobyWonum(wonum)
        parent.whatIfNotNull {
            _woCache.value = it
        }
    }

    fun fetchMasterLabor() {
        val masterLabor = laborRepository.fetchListLabor()
        masterLabor.whatIfNotNullOrEmpty {
            _getLaborMaster.value = it
        }
    }

    fun fetchListMasterCraft() {
        val masterCraft = laborRepository.fetchListCraftMaster()
        masterCraft.whatIfNotNullOrEmpty {
            _getCraftMasterEntity.value = it
        }
    }

    fun fetchTask(wonum: String) {
        val taskList = taskRepository.findListTaskByWonum(wonum)
        taskList.whatIfNotNullOrEmpty {
            _taskList.value = it
        }
    }

    fun fetchMasterCraft() {
        val list = laborRepository.fetchMasterCraft()
        val tempCraftMasterEntities = mutableListOf<CraftMasterEntity>()
        list.whatIfNotNullOrEmpty { craftlist ->
            craftlist.forEach {
                val craftCache = CraftMasterEntity()
                craftCache.craft = it
                tempCraftMasterEntities.add(craftCache)
            }
        }

        tempCraftMasterEntities.whatIfNotNullOrEmpty {
            _getCraftMaster.value = it
        }
    }

    fun saveToLocalCache(
        laborCode: String,
        taskid: String,
        taskdesc: String,
        craft: String,
        wonum: String,
        workroderid: String
    ) {
        val laborPlanCache = LaborPlanEntity()

        if (laborCode != BaseParam.APP_DASH) {
            laborPlanCache.laborcode = laborCode
            laborPlanCache.craft = BaseParam.APP_DASH
        } else {
            laborPlanCache.laborcode = BaseParam.APP_DASH
            laborPlanCache.craft = craft
        }

        if (taskid != BaseParam.APP_NULL) {
            laborPlanCache.taskid = taskid
            laborPlanCache.taskDescription = taskdesc
            laborPlanCache.isTask = BaseParam.APP_TRUE
            //To checking refwonum task in local
            val taskEntity =
                taskRepository.findTaskByWoIdAndTaskId(workroderid.toInt(), taskid.toInt())
            taskEntity.whatIfNotNull {
                laborPlanCache.wonumTask = it.refWonum
            }

        } else {
            laborPlanCache.isTask = BaseParam.APP_FALSE
        }

        laborPlanCache.wonumHeader = wonum
        laborPlanCache.workorderid = workroderid
        laborPlanCache.syncUpdate = BaseParam.APP_FALSE
        laborRepository.saveLaborPlanCache(laborPlanCache)

        //if this wo already upload to mx, this labor plan can automatic upload to mx
        createLaborPlantoMaximo(laborPlanCache)
    }

    fun updateToLocalCache(
        laborPlanEntity: LaborPlanEntity,
        laborCode: String,
        taskid: String,
        taskdesc: String,
        taskrefwonum: String,
        craft: String,
    ) {
        val workorderid = laborPlanEntity.workorderid.toString()

        if (taskid != BaseParam.APP_NULL) {
            laborPlanEntity.taskid = taskid
            laborPlanEntity.taskDescription = taskdesc
            laborPlanEntity.wonumTask = taskrefwonum
            laborPlanEntity.isTask = BaseParam.APP_TRUE
        } else {
            laborPlanEntity.isTask = BaseParam.APP_FALSE
        }

        if (laborCode != BaseParam.APP_DASH) {
            laborPlanEntity.laborcode = laborCode
            laborPlanEntity.craft = BaseParam.APP_DASH
        } else {
            laborPlanEntity.laborcode = BaseParam.APP_DASH
            laborPlanEntity.craft = craft
        }
        laborRepository.saveLaborPlanCache(laborPlanEntity)

        if (laborPlanEntity.syncUpdate == BaseParam.APP_TRUE) {
            val prepareBody = laborRepository.prepareBodyUpdateLaborPlanTomaximo(laborPlanEntity)
            prepareBody.whatIfNotNull {
                updateLaborPlanToMaximo(it, workorderid, laborPlanEntity)
            }
        }
    }

    //This method for update labor plan existing to maximo
    fun updateLaborPlanToMaximo(
        member: Member,
        workroderid: String,
        laborPlanEntity: LaborPlanEntity
    ) {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType: String = ("application/json")
        val patchType: String = BaseParam.APP_MERGE

        viewModelScope.launch(Dispatchers.IO) {
            workOrderRepository.updateLaborPlan(cookie,
                xMethodeOverride,
                contentType,
                patchType,
                workroderid.toInt(),
                member,
                onSuccess = {
                    Timber.tag(TAG).i("updateLaborPlanToMaximo() update local cache after update")
                    laborRepository.handlingUpdateLaborPlan(
                        laborPlanEntity,
                        BaseParam.APP_TRUE,
                        BaseParam.APP_TRUE
                    )
                },
                onError = {
                    Timber.tag(TAG).i("updateLaborPlanToMaximo() onError() onError: %s", it)
                    laborRepository.handlingUpdateLaborPlan(
                        laborPlanEntity,
                        BaseParam.APP_FALSE,
                        BaseParam.APP_TRUE
                    )

                }
            )
        }
    }

    fun createLaborPlantoMaximo(laborPlanEntity: LaborPlanEntity) {
        val wonum = laborPlanEntity.wonumHeader
        //checking wo cache avaiable
        val woCacheExisting = workOrderRepository.findWobyWonum(wonum.toString())
        woCacheExisting.whatIfNotNull {
            if (it.syncStatus == BaseParam.APP_TRUE) {
                //Prepare Body Update to Maximo
                prepareBodyCreateLaborPlan(laborPlanEntity)
            }
            laborPlanEntity.isLocally = BaseParam.APP_TRUE
            laborRepository.saveLaborPlanCache(laborPlanEntity)
        }
    }

    fun prepareBodyCreateLaborPlan(laborPlanEntity: LaborPlanEntity) {
        val workroderid = laborPlanEntity.workorderid
        val member = Member()
        workroderid.whatIfNotNull {
            val taskList = taskRepository.prepareBodyForCreateLaborPlanWithTask(it.toInt())
            val laborplanList = laborRepository.prepareBodyLaborPlan(it)

            taskList.whatIfNotNullOrEmpty { tasks ->
                member.woactivity = tasks
            }

            laborplanList.whatIfNotNullOrEmpty { wplabors ->
                member.wplabor = wplabors
            }

            member.whatIfNotNull { prepareBody ->
                updateCreateLaborPlanToMaximo(prepareBody, it, laborPlanEntity)
            }
        }
    }

    //This method for create labor plan to maximo
    fun updateCreateLaborPlanToMaximo(
        member: Member,
        workroderid: String,
        laborPlanEntity: LaborPlanEntity
    ) {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType: String = ("application/json")
        val patchType: String = BaseParam.APP_MERGE
        val properties: String = BaseParam.APP_ALL_PROPERTIES

        viewModelScope.launch(Dispatchers.IO) {
            workOrderRepository.createLaborPlan(cookie,
                xMethodeOverride,
                contentType,
                patchType,
                properties,
                workroderid.toInt(),
                member,
                onSuccess = { response ->
                    response.whatIfNotNull {
                        laborRepository.handlingCreateLaborPlan(it, laborPlanEntity)
                    }
                },
                onError = {
                    Timber.tag(TAG).i("createLaborPlanToMaximo() onError() onError: %s", it)
                }
            )
        }
    }

    fun removeLaborPlanEntity(laborPlanEntity: LaborPlanEntity, woCache: Boolean) {
        laborRepository.removeLaborPlanByEntity(laborPlanEntity)
        if(woCache) {
            //TODO Remove labor plan with api delete
            val localref = laborPlanEntity.localRef
            localref.whatIfNotNull {
                deleteLaborPlanFromMx(it, laborPlanEntity)
            }

        }
    }

    private fun deleteLaborPlanFromMx(locaref : String, laborPlanEntity: LaborPlanEntity) {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        viewModelScope.launch(Dispatchers.IO) {
            workOrderRepository.deleteLaborPlan(
                cookie, locaref,
                onSuccess = {
                    Timber.tag(TAG).i("deleteLaborPlanFromMx() onSuccess()")

                },
                onError = {
                    Timber.tag(TAG).i("deleteLaborPlanFromMx() onError() onError: %s", it)
                }

            )
        }
    }
}