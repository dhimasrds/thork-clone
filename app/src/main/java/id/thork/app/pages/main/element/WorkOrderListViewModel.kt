package id.thork.app.pages.main.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.work_order.Member
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.dao.WoCacheDaoImp
import id.thork.app.repository.WoPagingSource
import id.thork.app.repository.WorkOrderRepository
import kotlinx.coroutines.flow.Flow

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderListViewModel @ViewModelInject constructor(
    private val appSession: AppSession,
    private val workOrderRepository: WorkOrderRepository,
) : LiveCoroutinesViewModel() {

    private val woCacheDao : WoCacheDao
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
    private val _getWoList = MutableLiveData<List<Member>>()
    val getWoList : LiveData<List<Member>>get() = _getWoList

init {
    woCacheDao = WoCacheDaoImp()
}

    val woList: Flow<PagingData<Member>> = Pager(PagingConfig(pageSize = 10)) {
        WoPagingSource(appSession,workOrderRepository,woCacheDao)
    }.flow
        .cachedIn(viewModelScope)

//    fun fetchWoList() {
//        val laborcode: String? = appSession.laborCode
//        val select: String = ApiParam.WORKORDER_SELECT
//        val where: String =
//            ApiParam.WORKORDER_WHERE_LABORCODE_NEW + "\"" + laborcode + "\"" + ApiParam.WORKORDER_WHERE_STATUS + "}"
//        Timber.d("laborcode :%s", laborcode)
//
//        var response = WorkOrderResponse()
//        viewModelScope.launch(Dispatchers.IO) {
//            //fetch user data via API
//                workOrderRepository.getWorkOrderList(
//                    appSession.userHash!!, select, where,
//                    onSuccess = {
//                        response = it
//                        Timber.d("fetchWoList :%s", response.member!!.size)
//                        _getWoList.postValue(response.member)
//
//                    },
//                    onError = {
//                        _error.postValue(it)
//                    })
//            }
//
//        }
    }
