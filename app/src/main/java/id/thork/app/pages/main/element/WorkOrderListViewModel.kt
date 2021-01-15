package id.thork.app.pages.main.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.network.ApiParam
import id.thork.app.network.api.WorkOrderApi
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.network.model.user.ResponseInfo
import id.thork.app.network.model.user.UserResponse
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.entity.UserEntity_.laborcode
import id.thork.app.repository.WorkOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderListViewModel @ViewModelInject constructor(
    private  val appSession: AppSession,
    private  val workOrderRepository: WorkOrderRepository
)
: LiveCoroutinesViewModel() {

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
     val getWoList =MutableLiveData<List<Member>>()



     fun initWo() {
        val laborcode: String? = appSession.laborCode
        val select: String = ApiParam.WORKORDER_SELECT
        val where: String =
            ApiParam.WORKORDER_WHERE_LABORCODE_NEW + "\"" + laborcode + "\"" + ApiParam.WORKORDER_WHERE_STATUS + "}"

        fetchWoList(select, where)
    }

    private fun fetchWoList(select: String, where: String) {
        var response = WorkOrderResponse()
        viewModelScope.launch(Dispatchers.IO) {
            //fetch user data via API
            workOrderRepository.getWorkOrderList(
                appSession.userHash!!, select, where,
                onSuccess = {
                    response = it
                   getWoList.value = response.member
                },
                onError = {
                    _error.postValue(it)
                })

        }
    }
}