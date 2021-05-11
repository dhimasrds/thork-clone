package id.thork.app.pages.main.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.squareup.moshi.Moshi
import id.thork.app.base.BaseApplication.Constants.context
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppResourceMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.ApiParam
import id.thork.app.network.response.asset_response.AssetResponse
import id.thork.app.network.response.asset_response.Member
import id.thork.app.network.response.firebase.FirebaseAndroid
import id.thork.app.network.response.firebase.FirebaseBody
import id.thork.app.network.response.firebase.FirebaseData
import id.thork.app.network.response.firebase.ResponseFirebase
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
) : LiveCoroutinesViewModel() {
    val TAG = MapViewModel::class.java.name

    private val workManager = WorkManager.getInstance(context)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    private val _listWo = MutableLiveData<List<WoCacheEntity>>()

    private val _listAsset = MutableLiveData<List<AssetEntity>>()

    private val _listAssetFromMember = MutableLiveData<List<Member>>()

    val listWo: LiveData<List<WoCacheEntity>> get() = _listWo

    val listAsset: LiveData<List<AssetEntity>> get() = _listAsset

    val listAssetFromMember: LiveData<List<Member>> get() = _listAssetFromMember

    init {
        outputWorkInfos = workManager.getWorkInfosByTagLiveData("CREW_POSITION")
    }

    fun fetchListWo() {
        Timber.d("MapViewModel() fetchListWo")
        val listWoLocal = workOrderRepository.fetchWoList()
        _listWo.value = listWoLocal
    }

    private fun fetchListAsset() {
        Timber.d("MapViewModel() fetchListAsset")
        val listLocalAsset = workOrderRepository.fetchAssetList()
        _listAsset.postValue(listLocalAsset)
    }

    fun pruneWork() {
        Timber.d("MapViewModel() pruneWork")
        workManager.pruneWork()
    }

    fun deleteAssetEntity() {
        workOrderRepository.deleteAssetEntity()
    }

    fun postCrewPosition(
        latitude: String,
        longitude: String,
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

    fun fetchAsset() {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val select: String = ApiParam.WORKORDER_SELECT
        val savedQuery = appResourceMx.fsmResAsset

        deleteAssetEntity()
        viewModelScope.launch(Dispatchers.IO) {
            workOrderRepository.getAssetList(cookie, savedQuery!!, select,
                onSuccess = {
                    workOrderRepository.addAssetToObjectBox(it.member!!)
                    fetchListAsset()
                    Timber.d("fetchAsset() :%s", it.member)
                },
                onError = {
                    Timber.d("fetchAsset() :%s", it)
                },
                onException = {
                    Timber.d("fetchAsset() :%s", it)
                })
        }
    }

}