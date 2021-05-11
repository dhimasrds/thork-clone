package id.thork.app.pages.main.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.squareup.moshi.Moshi
import id.thork.app.base.BaseApplication.Constants.context
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppResourceMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.ApiParam
import id.thork.app.network.response.asset_response.AssetResponse
import id.thork.app.network.response.asset_response.Serviceaddress
import id.thork.app.network.response.firebase.FirebaseAndroid
import id.thork.app.network.response.firebase.FirebaseBody
import id.thork.app.network.response.firebase.FirebaseData
import id.thork.app.network.response.firebase.ResponseFirebase
import id.thork.app.persistence.dao.AssetDao
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.FirebaseRepository
import id.thork.app.repository.WorkOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 09/02/21
 * Jakarta, Indonesia.
 */
class MapViewModel @ViewModelInject constructor(
    private val workOrderRepository: WorkOrderRepository,
    private val firebaseRepository: FirebaseRepository,
    private val appSession: AppSession,
    private val preferenceManager: PreferenceManager,
    private val appResourceMx: AppResourceMx,
    private val assetDao: AssetDao,
    ) : LiveCoroutinesViewModel() {
    val TAG = MapViewModel::class.java.name

    private val workManager = WorkManager.getInstance(context)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    private val _listWo = MutableLiveData<List<WoCacheEntity>>()

    private val _listAsset = MutableLiveData<List<AssetEntity>>()

    val listWo: LiveData<List<WoCacheEntity>> get() = _listWo

    val listAsset: LiveData<List<AssetEntity>> get() = _listAsset

    var assetListObjectBox: HashMap<String, AssetEntity>? = null
    var assetResponse = AssetResponse()
    var error = false

    init {
        outputWorkInfos = workManager.getWorkInfosByTagLiveData("CREW_POSITION")
    }

    fun fetchListWo() {
        Timber.d("MapViewModel() fetchListWo")
        val listWoLocal = workOrderRepository.fetchWoList()
        _listWo.value = listWoLocal
    }

    fun fetchListAsset() {
        Timber.d("MapViewModel() fetchListAsset")
        val listWoLocal = workOrderRepository.fetchAssetList()
        _listAsset.value = listWoLocal
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

    fun fetchAsset(): Boolean {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val select: String = ApiParam.WORKORDER_SELECT
        val savedQuery = appResourceMx.fsmResAsset

        viewModelScope.launch(Dispatchers.IO) {
            workOrderRepository.getAssetList(cookie, savedQuery!!, select,
                onSuccess = {
                    assetResponse = it
                    assetResponse.member?.let { it1 -> checkingAssetInObjectBox(it1) }
                    Timber.d("raka :%s", it.member)
                    Timber.d("fetch asset paging source :%s", it.responseInfo)
                    Timber.d("fetch asset paging source :%s", it.member?.size)

                    error = false
                },
                onError = {
                    error = false
                },
                onException = {
                    error = true
                })
        }
        return error
    }

    private fun checkingAssetInObjectBox(list: List<id.thork.app.network.response.asset_response.Member>) {
        var listAsset: List<AssetEntity> = assetDao.findAllAsset()
        Timber.d("checkingWoInObjectBox savelocal :%s", listAsset.size)
        if (listAsset.isEmpty()) {
            Timber.d("checkingWoInObjectBox savelocal :%s", list.size)
            addAssetToObjectBox(list)
        } else {
            Timber.d("checkingWoInObjectBox compare :%s", "TEST")
            addAssetObjectBoxToHashMap()
            compareAssetLocalWithServer(list)
        }
    }

    private fun addAssetToObjectBox(list: List<id.thork.app.network.response.asset_response.Member>) {
        for (asset in list) {
            var serviceaddress: Serviceaddress
            asset.serviceaddress.whatIfNotNullOrEmpty(
                whatIf = {
                    serviceaddress = it.get(0)
                    val address: String = serviceaddress.formattedaddress!!
                    val latitudey: Double = serviceaddress.latitudey!!
                    val longitudex: Double = serviceaddress.longitudex!!
                    Timber.d("raka %s", asset.thisfsmtagtime)
                    val assetEntity = AssetEntity(
                        assetnum = asset.assetnum,
                        description = asset.description,
                        status = asset.status,
                        assetLocation = asset.location,
                        formattedaddress = address,
                        siteid = asset.siteid,
                        orgid = asset.orgid,
                        latitudey = latitudey,
                        longitudex = longitudex,
                        assetRfid = asset.thisfsmrfid,
                        image = asset.imagelibref,
                        assetTagTime = asset.thisfsmtagtime
                    )
                    assetEntity.createdDate = Date()
                    assetEntity.createdBy = appSession.userEntity.username
                    assetEntity.updatedBy = appSession.userEntity.username
                    workOrderRepository.saveAssetList(assetEntity, appSession.userEntity.username)
                })
        }
    }

    private fun compareAssetLocalWithServer(list: List<id.thork.app.network.response.asset_response.Member>) {
        for (asset in list) {
            Timber.d("compareWoLocalWithServer : %s", asset.assetnum)
            if (assetListObjectBox!![asset.assetnum!!] != null) {

            } else {
                createNewAsset(asset)
            }
        }
    }

    private fun addAssetObjectBoxToHashMap() {
        Timber.d("queryObjectBoxToHashMap()")
        if (assetDao.findAllAsset().isNotEmpty()) {
            assetListObjectBox = HashMap<String, AssetEntity>()
            val cacheEntities: List<AssetEntity> = assetDao.findAllAsset()
            for (i in cacheEntities.indices) {
                if (cacheEntities[i].status != null
                    && cacheEntities[i].status.equals(BaseParam.OPERATING)
                ) {
                    assetListObjectBox!![cacheEntities[i].assetnum!!] = cacheEntities[i]
                    Timber.d("HashMap value: %s", assetListObjectBox!![cacheEntities[i].assetnum])
                }
            }
        }
    }

    private fun createNewAsset(asset: id.thork.app.network.response.asset_response.Member) {
        var serviceaddress: Serviceaddress
        asset.serviceaddress.whatIfNotNullOrEmpty(
            whatIf = {
                serviceaddress = it.get(0)
                val address: String = serviceaddress.formattedaddress!!
                val latitudey: Double = serviceaddress.latitudey!!
                val longitudex: Double = serviceaddress.longitudex!!
                Timber.d("raka %s", asset.thisfsmtagtime)
                val assetEntity = AssetEntity(
                    assetnum = asset.assetnum,
                    description = asset.description,
                    status = asset.status,
                    assetLocation = asset.location,
                    formattedaddress = address,
                    siteid = asset.siteid,
                    orgid = asset.orgid,
                    latitudey = latitudey,
                    longitudex = longitudex,
                    assetRfid = asset.thisfsmrfid,
                    image = asset.imagelibref,
                    assetTagTime = asset.thisfsmtagtime
                )
                assetEntity.createdDate = Date()
                assetEntity.createdBy = appSession.userEntity.username
                assetEntity.updatedBy = appSession.userEntity.username
                workOrderRepository.saveAssetList(assetEntity, appSession.userEntity.username)
            })
    }
}