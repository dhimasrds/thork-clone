package id.thork.app.pages.labor_actual.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.entity.LaborActualEntity
import id.thork.app.repository.LaborRepository
import id.thork.app.repository.TaskRepository
import id.thork.app.repository.WorkOrderRepository
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
        wonum : String,
        woId  : String,
        labor :String,
        craft : String,
        startDateForMaximo : String,
        endDateForMaximo : String,
        skillLevel : String,
        startDate : String,
        startTime : String,
        endDate : String,
        endTime : String,){

        val laborActual = LaborActualEntity()
        laborActual.wonumHeader = wonum
        laborActual.workorderid = woId
        laborActual.laborcode = labor
        laborActual.craft = craft
        laborActual.startDate = startDate
        laborActual.endDate = endDate
        laborActual.statTime = startTime
        laborActual.endTime = endTime
        laborActual.startDateForMaximo = startDateForMaximo
        laborActual.endDateForMaximo = endDateForMaximo
        laborActual.skillLevel = skillLevel

        laborRepository.saveLaborActualCache(laborActual)

    }

    fun updateLaborActualLocal(
        laborActualEntity: LaborActualEntity,
        wonum : String,
        woId  : String,
        labor :String,
        craft : String,
        startDateForMaximo : String,
        endDateForMaximo : String,
        skillLevel : String,
        startDate : String,
        startTime : String,
        endDate : String,
        endTime : String,){

        laborActualEntity.wonumHeader = wonum
        laborActualEntity.workorderid = woId
        laborActualEntity.laborcode = labor
        laborActualEntity.craft = craft
        laborActualEntity.startDate = startDate
        laborActualEntity.endDate = endDate
        laborActualEntity.statTime = startTime
        laborActualEntity.endTime = endTime
        laborActualEntity.startDateForMaximo = startDateForMaximo
        laborActualEntity.endDateForMaximo = endDateForMaximo
        laborActualEntity.skillLevel = skillLevel

        laborRepository.saveLaborActualCache(laborActualEntity)

    }

    fun fetchListLaborActual(woId: String){
        val listLaborActual = laborRepository.findListLaborActualByWorkorderid(woId)
        Timber.d("fetchListLaborActual size :%s", listLaborActual.size)
        _getLaborActualList.value = listLaborActual
    }

    fun fetchLaborActualByObjectboxid(objectboxid : Long) {
        val laborCache = laborRepository.findlaborActualByObjectboxId(objectboxid)
        laborCache.whatIfNotNull {
            _getLaborActual.value = it
        }
    }

}