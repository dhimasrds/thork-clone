package id.thork.app.pages.main.element

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.squareup.moshi.Moshi
import id.thork.app.base.BaseApplication.Constants.context
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppResourceMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.ApiParam
import id.thork.app.network.response.firebase.FirebaseAndroid
import id.thork.app.network.response.firebase.FirebaseBody
import id.thork.app.network.response.firebase.FirebaseData
import id.thork.app.network.response.firebase.ResponseFirebase
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.LocationDao
import id.thork.app.persistence.dao.LocationDaoImp
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.FirebaseRepository
import id.thork.app.repository.WorkOrderRepository
import id.thork.app.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber

/**
 * Created by M.Reza Sulaiman on 09/02/21
 * Jakarta, Indonesia.
 */
class MapViewModel @ViewModelInject constructor(
    private val workOrderRepository: WorkOrderRepository,
    private val firebaseRepository: FirebaseRepository,
    private val appSession: AppSession,
    private val appResourceMx: AppResourceMx,
    private val preferenceManager: PreferenceManager
) : LiveCoroutinesViewModel() {
    val TAG = MapViewModel::class.java.name

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val locationDao: LocationDao

    init {
        locationDao = LocationDaoImp()
    }

    private val workManager = WorkManager.getInstance(context)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>
    var woListObjectBox: HashMap<String, WoCacheEntity>? = null

    private val _listWo = MutableLiveData<List<WoCacheEntity>>()
    private val _listMember = MutableLiveData<List<Member>>()
    private val _listMemberLocation =
        MutableLiveData<List<id.thork.app.network.response.fsm_location.Member>>()
    private val _listMemberAsset =
        MutableLiveData<List<id.thork.app.network.response.asset_response.Member>>()

    val listWo: LiveData<List<WoCacheEntity>> get() = _listWo
    val listMember: LiveData<List<Member>> get() = _listMember
    val listMemberLocation: LiveData<List<id.thork.app.network.response.fsm_location.Member>> get() = _listMemberLocation
    val listMemberAsset: LiveData<List<id.thork.app.network.response.asset_response.Member>> get() = _listMemberAsset

    init {
        outputWorkInfos = workManager.getWorkInfosByTagLiveData("CREW_POSITION")
    }

    suspend fun isConnected() {
        if (appSession.isConnected) {
            fectlistWoOnline()
        } else {
            fetchListWoOffline()
        }
    }

    fun fetchListWoOffline() {
        Timber.d("MapViewModel() fetchListWo")
        val listWoLocal = workOrderRepository.fetchWoList()
        _listWo.value = listWoLocal
    }

    fun pruneWork() {
        Timber.d("MapViewModel() pruneWork")
        workManager.pruneWork()
    }

    fun postCrewPosition(
        latitude: String,
        longitude: String
    ) {
        Timber.d("MapViewModel() postCrewPosition")
        val firebaseAndroid = FirebaseAndroid()
        firebaseAndroid.priority = BaseParam.APP_PRIORITY

        val firebaseData = FirebaseData()
        firebaseData.crewId = appSession.personUID.toString()
        firebaseData.longitude = longitude
        firebaseData.latitude = latitude
        firebaseData.laborcode = appSession.laborCode
        firebaseData.tag = BaseParam.APP_MOVE

        val firebaseBody = FirebaseBody()
        firebaseBody.to = BaseParam.APP_FIREBASE_TOPIC
        firebaseBody.firebaseData = firebaseData
        firebaseBody.firebaseAndroid = firebaseAndroid

        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(
            FirebaseBody::class.java
        )
        val jsonBody = jsonAdapter.toJson(firebaseBody)

        val body = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
        val contentType = ("application/json")
        var responsefirebase = ResponseFirebase()

        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepository.updateCrewPosition(BaseParam.APP_APIKEY_FIREBASE, contentType, body,
                onSuccess = {
                    responsefirebase = it
                },
                onError = {

                })
        }
    }

    suspend fun fectlistWoOnline() {
        Timber.d("MapViewModel() fetchListWo Online")
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val select: String = ApiParam.WORKORDER_SELECT
        val savedQuery = appResourceMx.fsmResWorkorder
        var response = WorkOrderResponse()

        savedQuery?.let {
            workOrderRepository.getWorkOrderList(
                cookie, it, select, pageno = 1, pagesize = 10,
                onSuccess = {
                    response = it
                    _listMember.postValue(it.member)
                    checkingWoInObjectBox(it.member)
                },
                onError = {
                    Timber.d("MapViewModel() fetchListWo Online onError: %s", it)

                    _error.postValue(it)
                },
                onException = {
                    Timber.d("MapViewModel() fetchListWo Online onException: %s", it)

                    _error.postValue(it)
                })
        }
        fetchLocationMarker()
    }

    private fun checkingWoInObjectBox(list: List<Member>?) {
        list.whatIfNotNullOrEmpty {
            if (workOrderRepository.fetchWoList().isEmpty()) {
                Timber.tag(TAG).d("checkingWoInObjectBox()")
                workOrderRepository.addWoToObjectBox(it)
            } else {
                addObjectBoxToHashMap()
                compareWoLocalWithServer(it)
            }
        }

    }

    private fun addObjectBoxToHashMap() {
        Timber.d("queryObjectBoxToHashMap()")
        if (workOrderRepository.fetchWoList().isNotEmpty()) {
            woListObjectBox = HashMap<String, WoCacheEntity>()
            val cacheEntities: List<WoCacheEntity> = workOrderRepository.fetchWoList()
            for (i in cacheEntities.indices) {
                woListObjectBox!![cacheEntities[i].wonum!!] = cacheEntities[i]
                Timber.d("HashMap value: %s", woListObjectBox!![cacheEntities[i].wonum])
            }
        }
    }


    @SuppressLint("NewApi")
    private fun compareWoLocalWithServer(list: List<id.thork.app.network.response.work_order.Member>) {
        for (wo in list) {
            if (woListObjectBox!![wo.wonum!!] != null) {
                val woCahce = workOrderRepository.findWobyWonum(wo.wonum.toString())
                val dateMaximo = StringUtils.convertTimeString(wo.changedate.toString())
                val dateWoCache = StringUtils.convertTimeString(woCahce?.changeDate.toString())
                Timber.tag(TAG).d("compareWoLocalWithServer() date Maximo convert: ${dateMaximo}")
                if (woCahce?.syncStatus?.equals(BaseParam.APP_TRUE) == true && dateMaximo > dateWoCache) {
                    Timber.tag(TAG).d("compareWoLocalWithServer() replace wo local cache")
                    workOrderRepository.replaceWolocalChace(woCahce, wo)
                }
            } else {
                Timber.tag(TAG).d("compareWoLocalWithServer() add new Wo")
                workOrderRepository.addWoToObjectBox(wo)
            }
        }
    }

    suspend fun fetchLocationMarker() {
        val cookie = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val select: String = ApiParam.WORKORDER_SELECT
        val savedQuery = appResourceMx.fsmResLocations
        workOrderRepository.deleteLocation()
        savedQuery.whatIfNotNull {
            viewModelScope.launch(Dispatchers.IO) {
                workOrderRepository.locationMarker(cookie,
                    it,
                    select,
                    onSuccess = { fsmLocation ->
                        fsmLocation.member.whatIfNotNullOrEmpty {
                            workOrderRepository.addLocationToObjectBox(it)
                            _listMemberLocation.postValue(it)
                        }
                    },
                    onError = {
                        Timber.tag(TAG).i("fetchLocationMarker() error: %s", it)
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
        workOrderRepository.deleteAssetEntity()
        savedQuery.whatIfNotNull {
            viewModelScope.launch(Dispatchers.IO) {
                workOrderRepository.getAssetList(cookie, it, select,
                    onSuccess = { assetResponse ->
                        assetResponse.member.whatIfNotNullOrEmpty {
                            workOrderRepository.addAssetToObjectBox(it)
                            _listMemberAsset.postValue(it)
                        }
                    },
                    onError = {
                        Timber.d("fetchAsset() onError :%s", it)
                        _error.postValue(it)
                    },
                    onException = {
                        Timber.d("fetchAsset() onException :%s", it)
                        _error.postValue(it)
                    })
            }
        }
    }
}