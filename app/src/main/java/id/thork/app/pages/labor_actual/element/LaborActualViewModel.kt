package id.thork.app.pages.labor_actual.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.response.work_order.Labtran
import id.thork.app.persistence.entity.LaborActualEntity
import id.thork.app.repository.LaborRepository
import id.thork.app.repository.TaskRepository
import id.thork.app.repository.WorkOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 29/07/21
 * Jakarta, Indonesia.
 */
class LaborActualViewModel @ViewModelInject constructor(

    private val laborRepository: LaborRepository,
    private val workOrderRepository: WorkOrderRepository,
    private val taskRepository: TaskRepository,
    private val preferenceManager: PreferenceManager,

    ) : LiveCoroutinesViewModel() {
    val TAG = LaborActualViewModel::class.java.name

    private val _getLaborActualList = MutableLiveData<List<LaborActualEntity>>()
    val getLaborActualList: LiveData<List<LaborActualEntity>> get() = _getLaborActualList

    private val _getLaborActual = MutableLiveData<LaborActualEntity>()
    val getLaborActual: LiveData<LaborActualEntity> get() = _getLaborActual

    fun saveLaborActualLocal(
        taskid: String,
        taskdesc: String,
        wonum: String,
        woId: String,
        labor: String,
        craft: String,
        startDateForMaximo: String,
        skillLevel: String,
        startDate: String,
        startTime: String,
    ) {

        val laborActual = LaborActualEntity()
        laborActual.wonumHeader = wonum
        laborActual.workorderid = woId
        laborActual.laborcode = labor
        laborActual.craft = craft
        laborActual.startDate = startDate
        laborActual.statTime = startTime
        laborActual.startDateForMaximo = startDateForMaximo
        laborActual.skillLevel = skillLevel
        laborActual.syncUpdate = BaseParam.APP_FALSE
        laborActual.isLocally = BaseParam.APP_TRUE

        if (taskid != BaseParam.APP_NULL) {
            laborActual.taskid = taskid
            laborActual.taskDescription = taskdesc
            laborActual.isTask = BaseParam.APP_TRUE
            val taskEntity =
                taskRepository.findTaskByWoIdAndTaskId(woId.toInt(), taskid.toInt())
            taskEntity.whatIfNotNull {
                laborActual.wonumTask = it.refWonum
            }
        } else {
            laborActual.isTask = BaseParam.APP_FALSE
        }
        laborRepository.saveLaborActualCache(laborActual)
        //TODO Need handling when case offline mode
        prepareBodyCreateLaborActual(laborActual)

    }

    fun updateLaborActual(
        laborActualEntity: LaborActualEntity,
        startDateForMaximo: String,
        endDateForMaximo: String,
        startDate: String,
        startTime: String,
        endDate: String,
        endTime: String,
    ) {
        laborActualEntity.startDateForMaximo = startDateForMaximo
        laborActualEntity.endDateForMaximo = endDateForMaximo
        laborActualEntity.startDate = startDate
        laborActualEntity.endDate = endDate
        laborActualEntity.statTime = startTime
        laborActualEntity.endTime = endTime
        laborActualEntity.syncUpdate = BaseParam.APP_FALSE
        laborActualEntity.isLocally = BaseParam.APP_TRUE
        laborRepository.saveLaborActualCache(laborActualEntity)

        //TODO Handling case offline mode
        prepareBodyUpdateLaborActual(laborActualEntity)
    }


    fun fetchListLaborActual(woId: String) {
        val listLaborActual = laborRepository.findListLaborActualByWorkorderid(woId)
        Timber.d("fetchListLaborActual size :%s", listLaborActual.size)
        _getLaborActualList.value = listLaborActual
    }

    fun fetchLaborActualByObjectboxid(objectboxid: Long) {
        val laborCache = laborRepository.findlaborActualByObjectboxId(objectboxid)
        laborCache.whatIfNotNull {
            _getLaborActual.value = it
        }
    }

    fun removeLaborActual(laborActualEntity: LaborActualEntity) {
        laborRepository.removeLaborActualByEntity(laborActualEntity)
    }

    fun prepareBodyCreateLaborActual(laborActualEntity: LaborActualEntity) {
        val workorderid = laborActualEntity.workorderid
        val member = id.thork.app.network.response.work_order.Member()

        workorderid.whatIfNotNull {
            val tasklist = taskRepository.prepareBodyForCreateLaborActualWithTask(it.toInt())
            val labtrans = laborRepository.prepareBodyLaborActualNonTask(it)

            tasklist.whatIfNotNullOrEmpty { tasks ->
                member.woactivity = tasks
            }

            labtrans.whatIfNotNullOrEmpty { labtran ->
                member.labtrans = labtran
            }

            member.whatIfNotNull { preparebody ->
                createLaborActualToMx(preparebody, it, laborActualEntity)
            }
        }
    }

    fun createLaborActualToMx(
        member: id.thork.app.network.response.work_order.Member,
        workroderid: String,
        laborActualEntity: LaborActualEntity
    ) {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType: String = ("application/json")
        val patchType: String = BaseParam.APP_MERGE
        val properties: String = BaseParam.APP_ALL_PROPERTIES

        viewModelScope.launch(Dispatchers.IO) {
            workOrderRepository.createLaborActual(
                cookie,
                xMethodeOverride,
                contentType,
                patchType,
                properties,
                workroderid.toInt(),
                member,
                onSuccess = { response ->
                    response.whatIfNotNull {
                        Timber.tag(TAG).i("createLaborActualToMx() onResponse() %s", it)
                        laborRepository.handlingCreateLaborActual(it, laborActualEntity)
                    }

                },
                onError = {
                    Timber.tag(TAG).i("createLaborActualToMx() onError() onError: %s", it)
                }
            )
        }
    }

    fun prepareBodyUpdateLaborActual(laborActualEntity: LaborActualEntity) {
        val labtransid = laborActualEntity.labtransid
        val labtran = laborRepository.prepareBodyUpdateLaborActual(laborActualEntity)

        labtran.whatIfNotNull {
            labtransid.whatIfNotNull { labtransid ->
                updateLaborActualToMx(it, labtransid, laborActualEntity)
            }
        }
    }

    fun updateLaborActualToMx(
        labtran: Labtran,
        labtransid: String,
        laborActualEntity: LaborActualEntity
    ) {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType: String = ("application/json")
        val patchType: String = BaseParam.APP_MERGE

        viewModelScope.launch(Dispatchers.IO) {
            workOrderRepository.updateLaborActual(
                cookie,
                xMethodeOverride,
                contentType,
                patchType,
                labtransid.toInt(),
                labtran,
                onSuccess = {
                    Timber.tag(TAG).i("updateLaborActualToMx() update local cache after update")
                    laborRepository.handlingUpdateLaborActual(laborActualEntity, BaseParam.APP_TRUE)

                },
                onError = {
                    Timber.tag(TAG).i("updateLaborActualToMx() onError() onError: %s", it)

                }
            )
        }
    }
}