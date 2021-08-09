package id.thork.app.pages.labor_plan.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.*
import id.thork.app.repository.LaborRepository
import id.thork.app.repository.TaskRepository
import id.thork.app.repository.WorkOrderRepository
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 23/06/21
 * Jakarta, Indonesia.
 */
class LaborPlanViewModel @ViewModelInject constructor(
    private val laborRepository: LaborRepository,
    private val workOrderRepository: WorkOrderRepository,
    private val taskRepository: TaskRepository

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

    fun fetchMasterCraft(laborcode: String) {
        val masterCraft = laborRepository.craftMaster(laborcode)
        masterCraft.whatIfNotNullOrEmpty {
            _getCraftMaster.value = it
        }
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
        for (a in list!!){
            Timber.d("fetchMasterCraft() craft :%s", a)
        }
        Timber.d("fetchMasterCraft() :%s", list.size)
    }

    fun saveToLocalCache(
        laborCode: String,
        taskid: String,
        taskdesc: String,
        craft: String,
        skillLevel: String,
        wonum: String,
        workroderid: String
    ) {
        val laborPlanCache = LaborPlanEntity()
        laborPlanCache.laborcode = laborCode
        laborPlanCache.taskid = taskid
        laborPlanCache.taskDescription = taskdesc
        laborPlanCache.craft = craft
        laborPlanCache.skillLevel = skillLevel
        laborPlanCache.wonumHeader = wonum
        laborPlanCache.workorderid = workroderid
        laborPlanCache.syncUpdate = BaseParam.APP_FALSE
        laborRepository.saveLaborPlanCache(laborPlanCache)
    }

}