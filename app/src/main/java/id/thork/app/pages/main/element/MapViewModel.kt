package id.thork.app.pages.main.element

import android.annotation.SuppressLint
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
import id.thork.app.network.response.work_order.Assignment
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.network.response.fsm_location.Member
import id.thork.app.persistence.dao.LocationDao
import id.thork.app.persistence.dao.LocationDaoImp
import id.thork.app.persistence.entity.LocationEntity
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.FirebaseRepository
import id.thork.app.repository.WorkOrderRepository
import id.thork.app.utils.StringUtils
import id.thork.app.utils.WoUtils
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

    private val locationDao : LocationDao

    init {
        locationDao = LocationDaoImp()
    }


    private val workManager = WorkManager.getInstance(context)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>
    var woListObjectBox: HashMap<String, WoCacheEntity>? = null


    private val _listWo = MutableLiveData<List<WoCacheEntity>>()
    private val _listMember = MutableLiveData<List<Member>>()


    val listWo: LiveData<List<WoCacheEntity>> get() = _listWo
    val listMember: LiveData<List<Member>> get() = _listMember

    private val _location = MutableLiveData<List<LocationEntity>>()

    val location: LiveData<List<LocationEntity>> get() = _location

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
                    _listMember.postValue(response.member)
                    checkingWoInObjectBox(response.member)
                },
                onError = {
                },
                onException = {
                })
        }
    }

    private fun checkingWoInObjectBox(list: List<Member>) {
        if (workOrderRepository.fetchWoList().isEmpty()) {
            Timber.tag(TAG).d("checkingWoInObjectBox()")
            addWoToObjectBox(list)
        } else {
            addObjectBoxToHashMap()
            compareWoLocalWithServer(list)
        }
    }

    private fun addWoToObjectBox(list: List<Member>) {
        for (wo in list) {
            Timber.tag(TAG).d("addWoToObjectBox()")
            createNewWo(wo)
        }
    }

    private fun setupWoLocation(woCacheEntity: WoCacheEntity, wo: Member) {
        woCacheEntity.latitude = if (!wo.woserviceaddress.isNullOrEmpty()) {
            wo.woserviceaddress!![0].latitudey
        } else {
            null
        }
        woCacheEntity.longitude = if (!wo.woserviceaddress.isNullOrEmpty()) {
            wo.woserviceaddress!![0].longitudex
        } else {
            null
        }
    }

    private fun createNewWo(wo: Member) {
        var assignment: Assignment
        wo.assignment.whatIfNotNullOrEmpty(
            whatIf = {
                assignment = it.get(0)
                val laborCode: String = assignment.laborcode!!
                val woCacheEntity = WoCacheEntity(
                    syncBody = WoUtils.convertMemberToBody(wo),
                    woId = wo.workorderid,
                    wonum = wo.wonum,
                    status = wo.status,
                    isChanged = BaseParam.APP_TRUE,
                    isLatest = BaseParam.APP_TRUE,
                    syncStatus = BaseParam.APP_FALSE,
                    laborCode = laborCode,
                    changeDate = wo.changedate
                )
                setupWoLocation(woCacheEntity, wo)
                woCacheEntity.createdDate = Date()
                woCacheEntity.createdBy = appSession.userEntity.username
                woCacheEntity.updatedBy = appSession.userEntity.username
                workOrderRepository.saveWoList(woCacheEntity, appSession.userEntity.username)
            })
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
    private fun compareWoLocalWithServer(list: List<Member>) {
        for (wo in list) {
            if (woListObjectBox!![wo.wonum!!] != null) {
                val woCahce = workOrderRepository.findWobyWonum(wo.wonum.toString())
                val dateMaximo = StringUtils.convertTimeString(wo.changedate.toString())
                val dateWoCache = StringUtils.convertTimeString(woCahce?.changeDate.toString())
                Timber.tag(TAG).d("compareWoLocalWithServer() date Maximo convert: ${dateMaximo}")
                if (woCahce?.syncStatus?.equals(BaseParam.APP_TRUE) == true && dateMaximo > dateWoCache) {
                    Timber.tag(TAG).d("compareWoLocalWithServer() replace wo local cache")
                    replaceWolocalChace(woCahce, wo)
                }
            } else {
                Timber.tag(TAG).d("compareWoLocalWithServer() add new Wo")
                createNewWo(wo)
            }
        }
    }

    private fun replaceWolocalChace(woCacheEntity: WoCacheEntity, member: Member) {
        woCacheEntity.syncBody = WoUtils.convertMemberToBody(member)
        woCacheEntity.woId = member.workorderid
        woCacheEntity.wonum = member.wonum
        woCacheEntity.status = member.status
        woCacheEntity.changeDate = member.changedate
        woCacheEntity.isChanged = BaseParam.APP_TRUE
        woCacheEntity.isLatest = BaseParam.APP_TRUE
        woCacheEntity.syncStatus = BaseParam.APP_FALSE
        woCacheEntity.updatedBy = appSession.userEntity.username
        workOrderRepository.saveWoList(woCacheEntity, appSession.userEntity.username)
    }
}