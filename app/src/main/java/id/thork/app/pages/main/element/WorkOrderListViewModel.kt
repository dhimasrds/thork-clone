package id.thork.app.pages.main.element

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.dao.WoCacheDaoImp
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.WorkOrderRepository
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderListViewModel @ViewModelInject constructor(
    private val appSession: AppSession,
    private val workOrderRepository: WorkOrderRepository,
    @Assisted state: SavedStateHandle
) : LiveCoroutinesViewModel() {

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val EMPTY_QUERY = ""
    }

    private val woCacheDao: WoCacheDao
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
    private val _getWoList = MutableLiveData<List<WoCacheEntity>>()
    val getWoList: LiveData<List<WoCacheEntity>> get() = _getWoList

    init {
        woCacheDao = WoCacheDaoImp()
    }

    private val currentQuery = state.getLiveData(CURRENT_QUERY, EMPTY_QUERY)
    val woList = currentQuery.switchMap { query ->
        if (!query.isEmpty()) {
            Timber.d("filter on  viewmodel :%s", query)
            workOrderRepository.getSearchWo(appSession, workOrderRepository, query)
        } else {
            Timber.d("filter off viewmodel :%s", query)
            workOrderRepository.getWoList(appSession, workOrderRepository).cachedIn(viewModelScope)
        }
    }

    fun searchWo(query: String) {
        currentQuery.value = query
    }

    fun checkingListWo() : List<WoCacheEntity>{
        return woCacheDao.findAllWo()
    }



}
