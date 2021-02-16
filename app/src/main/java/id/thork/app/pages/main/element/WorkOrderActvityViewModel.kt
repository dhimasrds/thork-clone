package id.thork.app.pages.main.element

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.work_order.Member
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.dao.WoCacheDaoImp
import id.thork.app.repository.WoActivityRepository
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 16/02/21
 * Jakarta, Indonesia.
 */
class WorkOrderActvityViewModel  @ViewModelInject constructor(
    private val appSession: AppSession,
    private val repository: WoActivityRepository,
    @Assisted state: SavedStateHandle
) : LiveCoroutinesViewModel() {

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val EMPTY_QUERY = ""
    }

    private val woCacheDao: WoCacheDao
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
    private val _getWoList = MutableLiveData<List<Member>>()
    val getWoList: LiveData<List<Member>> get() = _getWoList

    init {
        woCacheDao = WoCacheDaoImp()
    }

    private val currentQuery = state.getLiveData(CURRENT_QUERY, EMPTY_QUERY)
    val woList = currentQuery.switchMap { query ->
        if (!query.isEmpty()) {
            Timber.d("filter on  viewmodel :%s", query)
            repository.getSearchWo(appSession, repository, query)
        } else {
            Timber.d("filter off viewmodel :%s", query)
            repository.getWoList(appSession, repository).cachedIn(viewModelScope)
        }
    }

    fun searchWo(query: String) {
        currentQuery.value = query
    }

}
