package id.thork.app.pages.labor_plan.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.Worklog
import id.thork.app.pages.work_log.element.WorkLogViewModel
import id.thork.app.persistence.entity.LaborPlanEntity
import id.thork.app.persistence.entity.MultiAssetEntity
import id.thork.app.persistence.entity.WorklogEntity
import id.thork.app.persistence.entity.WorklogTypeEntity
import id.thork.app.repository.LaborRepository
import id.thork.app.repository.WorkOrderRepository
import id.thork.app.repository.WorklogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 23/06/21
 * Jakarta, Indonesia.
 */
class LaborPlanViewModel  @ViewModelInject constructor(
    private val laborRepository: LaborRepository

    ) : LiveCoroutinesViewModel() {
    val TAG = LaborPlanViewModel::class.java.name

    private val _getLaborPlanList = MutableLiveData<List<LaborPlanEntity>>()
    val getLaborPlanList: LiveData<List<LaborPlanEntity>> get() = _getLaborPlanList

    fun fetchLaborPlan(workroderid: String) {
        val listLabor = laborRepository.findListLaborplanWorkorderid(workroderid)
        listLabor.whatIfNotNullOrEmpty {
            _getLaborPlanList.value = it
        }
    }




}