package id.thork.app.pages.main.element

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppResourceMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.ApiParam
import id.thork.app.network.response.work_order.Member
import id.thork.app.pages.login.element.LoginViewModel
import id.thork.app.persistence.dao.AssetDao
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.dao.WoCacheDaoImp
import id.thork.app.repository.WoActivityRepository
import id.thork.app.repository.WorkOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 16/02/21
 * Jakarta, Indonesia.
 */
class WorkOrderActvityViewModel @ViewModelInject constructor(
    private val appSession: AppSession,
    private val repository: WoActivityRepository,
    private val preferenceManager: PreferenceManager,
    private val appResourceMx: AppResourceMx,
    private val assetDao: AssetDao,
    private val workOrderRepository: WorkOrderRepository,
    @Assisted state: SavedStateHandle,
) : LiveCoroutinesViewModel() {
    val TAG = WorkOrderActvityViewModel::class.java.name

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val EMPTY_QUERY = ""
    }

    private val woCacheDao: WoCacheDao
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
    private val _getWoList = MutableLiveData<List<Member>>()
    val getWoList: LiveData<List<Member>> get() = _getWoList

    private val workManager = WorkManager.getInstance(BaseApplication.context)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    init {
        woCacheDao = WoCacheDaoImp()
        outputWorkInfos = workManager.getWorkInfosByTagLiveData("SYNC_WO")
    }

    private val currentQuery = state.getLiveData(CURRENT_QUERY, EMPTY_QUERY)
    val woList = currentQuery.switchMap { query ->
        if (!query.isEmpty()) {
            Timber.d("filter on  viewmodel :%s", query)
            repository.getSearchWo(appSession,
                repository,
                query,
                preferenceManager,
                appResourceMx,
                workOrderRepository)
        } else {
            Timber.d("filter off viewmodel :%s", query)
            repository.getWoList(appSession,
                repository,
                preferenceManager,
                appResourceMx,
                workOrderRepository).cachedIn(viewModelScope)
        }
    }

    fun searchWo(query: String) {
        Timber.d("searchWo viewmodel :%s", query)
        currentQuery.value = query
    }

    fun pruneWork() {
        Timber.d("WorkOrderActvityViewModel() pruneWork")
        workManager.pruneWork()
    }
}
