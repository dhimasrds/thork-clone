package id.thork.app.pages.labor_plan.element

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

    fun fetchLaborPlanByLaborCode(laborcode: String, workorderid: String) {
        val laborCache = laborRepository.findLaborPlan(laborcode, workorderid)
        laborCache.whatIfNotNull {
            _laborPlanCache.value = it
        }
    }

    fun fetchLaborPlanByCraft(craft: String, workroderid: String) {
        val laborCache = laborRepository.findLaborPlanByCraft(craft, workroderid)
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

    fun fetchMasterCraftByLaborcode(laborcode: String) {
        val masterCraft = laborRepository.craftMaster(laborcode)
        masterCraft.whatIfNotNullOrEmpty(
            whatIf = {
                _getCraftMaster.value = it
            },
            whatIfNot = {
                fetchMasterCraft()
            }
        )
    }

    fun fetchTask(wonum: String) {
        val taskList = taskRepository.findListTaskByWonum(wonum)
        taskList.whatIfNotNullOrEmpty {
            _taskList.value = it
        }
    }

    fun fetchLaborAndCraft(laborcode: String) {
        val entity = laborRepository.fetchCraft(laborcode)
        entity.whatIfNotNull {
            _craftEntity.value = it
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
            val taskEntity = taskRepository.findTaskByWoIdAndTaskId(workroderid.toInt(), taskid.toInt())
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
            val prepareBody = prepareBodyUpdateTomaximo(laborPlanEntity)
            prepareBody.whatIfNotNull {
                updateLaborPlanToMaximo(it, workorderid, laborPlanEntity)
            }
        }
    }

    fun prepareBodyUpdateTomaximo(laborPlanEntity: LaborPlanEntity): Member {
        //need prepare body, and validation with task or without task
        val wplaborid = laborPlanEntity.wplaborid.toString()
        val taskid = laborPlanEntity.taskid.toString()
        val wonumTask = laborPlanEntity.wonumTask.toString()
        val laborCode = laborPlanEntity.laborcode.toString()
        val craft = laborPlanEntity.craft.toString()
        val prepareBody = Member()

        if (laborPlanEntity.isTask == BaseParam.APP_TRUE) {
            //Prepare body with task
            val bodyLaborplanWithTask =
                laborRepository.preapreBodyLaborPlanTask(wplaborid, taskid, wonumTask)
            bodyLaborplanWithTask.whatIfNotNullOrEmpty {
                prepareBody.woactivity = it
            }
        } else {
            //Prepare body without task
            val bodyLaborplanWithoutTask =
                laborRepository.preapreBodyLaborPlanNontask(wplaborid, laborCode, craft)
            bodyLaborplanWithoutTask.whatIfNotNullOrEmpty {
                prepareBody.wplabor = it
            }
        }
        return prepareBody
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
                        laborRepository.handlingUpdateLaborPlan(laborPlanEntity, BaseParam.APP_TRUE, BaseParam.APP_TRUE)
                },
                onError = {
                    Timber.tag(TAG).i("updateLaborPlanToMaximo() onError() onError: %s", it)
                        laborRepository.handlingUpdateLaborPlan(laborPlanEntity, BaseParam.APP_FALSE, BaseParam.APP_TRUE)

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
                prepareBodyAddLaborPlan(laborPlanEntity)
            }
            laborPlanEntity.isLocally = BaseParam.APP_TRUE
            laborRepository.saveLaborPlanCache(laborPlanEntity)
        }
    }

    fun prepareBodyAddLaborPlan(laborPlanEntity: LaborPlanEntity) {
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
                createLaborPlanToMaximo(prepareBody, it, laborPlanEntity)
            }
        }
    }

    //This method for create labor plan to maximo
    fun createLaborPlanToMaximo(
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
                    Timber.tag(TAG).i(
                        "createLaborPlanToMaximo() onSuccess() %s",
                        response
                    )
                    response.whatIfNotNull {
                        handlingCreateLaborPlan(it, laborPlanEntity)
                    }
                },
                onError = {
                    Timber.tag(TAG).i("createLaborPlanToMaximo() onError() onError: %s", it)
                }
            )
        }
    }

    fun handlingCreateLaborPlan(member: Member, laborPlanEntity: LaborPlanEntity) {
        val isTask = laborPlanEntity.isTask
        val listLpTask = member.woactivity
        val listLpNonTask = member.wplabor

        if (isTask == BaseParam.APP_TRUE) {
            listLpTask.whatIfNotNullOrEmpty { list ->
                list.forEach {
                    it.wplabor.whatIfNotNullOrEmpty { wplabors ->
                        wplabors.forEach { labor ->
                            val wplaborid = labor.wplaborid
                            laborPlanEntity.wonumTask = it.wonum
                            checkingLaborPlanExisting(wplaborid.toString(), laborPlanEntity)
                        }
                    }
                }
            }
        } else {
            listLpNonTask.whatIfNotNullOrEmpty {
                it.forEach { wplabor ->
                    val wplaborid = wplabor.wplaborid
                    checkingLaborPlanExisting(wplaborid.toString(), laborPlanEntity)
                }
            }
        }
    }

    fun handlingUpdateLaborPlan(laborPlanEntity: LaborPlanEntity, syncUpdate: Int, isLocally: Int) {
        laborPlanEntity.syncUpdate = syncUpdate
        laborPlanEntity.isLocally = isLocally
        laborRepository.saveLaborPlanCache(laborPlanEntity)
    }

    fun checkingLaborPlanExisting(wplaborid: String, laborPlanEntity: LaborPlanEntity) {
        val cacheLaborPlan = laborRepository.findLaborPlanByWplaborid(wplaborid)
        if (cacheLaborPlan == null) {
            Timber.tag(TAG).i("checkingLaborPlanExisting() update local cache after update")
            laborPlanEntity.wplaborid = wplaborid
            laborPlanEntity.syncUpdate = BaseParam.APP_TRUE
            laborPlanEntity.isLocally = BaseParam.APP_TRUE
            laborRepository.saveLaborPlanCache(laborPlanEntity)
        }
    }

    fun removeLaborPlanEntity(laborPlanEntity: LaborPlanEntity, woCache: Boolean) {
        laborRepository.removeLaborPlanByEntity(laborPlanEntity)
        //TODO need api delete labor plan from maximo
//        if(woCache) {
//            //TODO Remove labor plan with api delete
//
//        }
    }
}