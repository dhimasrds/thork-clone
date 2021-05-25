package id.thork.app.pages.main.element

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppResourceMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.ApiParam
import id.thork.app.persistence.dao.AssetDao
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.dao.WoCacheDaoImp
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.WorkOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderListViewModel @ViewModelInject constructor(
    private val appSession: AppSession,
    private val workOrderRepository: WorkOrderRepository,
    private val preferenceManager: PreferenceManager,
    private val appResourceMx: AppResourceMx,
    private val assetDao: AssetDao,
    @Assisted state: SavedStateHandle
) : LiveCoroutinesViewModel() {
    val TAG = WorkOrderListViewModel::class.java.name

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
            workOrderRepository.getSearchWo(appSession, workOrderRepository, query, preferenceManager, appResourceMx)
        } else {
            Timber.d("filter off viewmodel :%s", query)
            workOrderRepository.getWoList(appSession, workOrderRepository, preferenceManager, appResourceMx).cachedIn(viewModelScope)
        }
    }

    fun searchWo(query: String) {
        currentQuery.value = query
    }

    fun checkingListWo() : List<WoCacheEntity>{
        return woCacheDao.findAllWo()
    }

    fun fetchLocationMarker() {
        val cookie = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val select: String = ApiParam.WORKORDER_SELECT
        val savedQuery = appResourceMx.fsmResLocations
        Timber.tag(TAG).d("fetchLocationMarker() savedQuery %s", savedQuery)
        savedQuery.whatIfNotNull {
            viewModelScope.launch(Dispatchers.IO) {
                workOrderRepository.locationMarker(cookie,
                    it,
                    select,
                    onSuccess = { fsmLocation ->
                        fsmLocation.member.whatIfNotNullOrEmpty {
                            Timber.tag(TAG).i("loginViewModel() fetchLocationMarker() onSuccess: %s", it)
                            workOrderRepository.addLocationToObjectBox(it)
                        }
                    },
                    onError = {
                        Timber.tag(TAG).i("loginViewModel() fetchLocationMarker() error: %s", it)
                        _error.postValue(it)
                    })
            }
        }

        fetchAsset()
    }

    fun fetchAsset() {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val select: String = ApiParam.WORKORDER_SELECT
        val savedQuery = appResourceMx.fsmResAsset
        savedQuery.whatIfNotNull {
            viewModelScope.launch(Dispatchers.IO) {
                workOrderRepository.getAssetList(cookie, it, select,
                    onSuccess = { assetResponse ->
                        assetResponse.member.whatIfNotNullOrEmpty {
                            Timber.d("loginViewModel() fetchAsset() onSuccess :%s", it)
                            workOrderRepository.addAssetToObjectBox(it)
                        }
                    },
                    onError = {
                        Timber.d("loginViewModel() fetchAsset() onError :%s", it)
                        _error.postValue(it)
                    },
                    onException = {
                        Timber.d("loginViewModel() fetchAsset() onException :%s", it)
                        _error.postValue(it)
                    })
            }
        }
    }



}
