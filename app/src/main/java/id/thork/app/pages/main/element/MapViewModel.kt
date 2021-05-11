package id.thork.app.pages.main.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
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
import id.thork.app.network.response.fsm_location.Member
import id.thork.app.persistence.dao.LocationDao
import id.thork.app.persistence.dao.LocationDaoImp
import id.thork.app.persistence.entity.LocationEntity
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.FirebaseRepository
import id.thork.app.repository.WorkOrderRepository
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
    private val preferenceManager: PreferenceManager,
    private val appResourceMx: AppResourceMx,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {
    val TAG = MapViewModel::class.java.name

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val locationDao : LocationDao

    init {
        locationDao = LocationDaoImp()
    }


    private val workManager = WorkManager.getInstance(context)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    private val _listWo = MutableLiveData<List<WoCacheEntity>>()

    val listWo: LiveData<List<WoCacheEntity>> get() = _listWo

    private val _location = MutableLiveData<List<LocationEntity>>()

    val location: LiveData<List<LocationEntity>> get() = _location

    init {
        outputWorkInfos = workManager.getWorkInfosByTagLiveData("CREW_POSITION")
    }

    fun fetchListWo() {
        Timber.d("MapViewModel() fetchListWo")
        val listWoLocal = workOrderRepository.fetchWoList()
        _listWo.value = listWoLocal
    }

    fun fetchLocation(){
        val location = workOrderRepository.fetchLocalMarker()
        _location.value = location
    }

    suspend fun fetchLocationMarker() {
        val cookie = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        workOrderRepository.deleteLocation()
        viewModelScope.launch(Dispatchers.IO) {
            workOrderRepository.locationMarker(cookie,
                appResourceMx.fsmResLocations!!,
                ApiParam.WORKORDER_SELECT,
                onSuccess = { fsmLocation ->
                    fsmLocation.member.whatIfNotNullOrEmpty {
                        saveLocationToLocal(fsmLocation.member!!)
                    }
                },
                onError = {
                    Timber.tag(TAG).i("loginCookie() error: %s", it)
                    _error.postValue(it)
                })

        }
//
    }

    private fun saveLocationToLocal(member: List<Member>) {
        for (location in member) {
            val locationEntity = LocationEntity()
            locationEntity.location = location.location
            locationEntity.description = location.description
            locationEntity.status = location.status
            location.serviceaddress.whatIfNotNullOrEmpty {
                locationEntity.formatAddress = location.serviceaddress!![0].formattedaddress
                locationEntity.longitudex = location.serviceaddress!![0].longitudex
                locationEntity.latitudey = location.serviceaddress!![0].latitudey
            }
            locationEntity.thisfsmrfid = location.thisfsmrfid
            locationEntity.image = location.imagelibref
            locationEntity.thisfsmtagprogress = location.thisfsmtagprogress.toString()
            locationEntity.thisfsmtagtime = location.thisfsmtagtime
            workOrderRepository.saveLocationToLocal(locationEntity)
        }
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
}