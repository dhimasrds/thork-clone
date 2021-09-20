package id.thork.app.pages.detail_wo.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.helper.MapsLocation
import id.thork.app.network.response.google_maps.ResponseRoute
import id.thork.app.network.response.work_order.Member
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.persistence.entity.MaterialBackupEntity
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.*
import id.thork.app.utils.MapsUtils
import id.thork.app.utils.WoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by M.Reza Sulaiman on 11/02/21
 * Jakarta, Indonesia.
 */
class DetailWoViewModel @ViewModelInject constructor(
    private val workOrderRepository: WorkOrderRepository,
    private val googleMapsRepository: GoogleMapsRepository,
    private val woActivityRepository: WoActivityRepository,
    private val materialRepository: MaterialRepository,
    private val appSession: AppSession,
    private val preferenceManager: PreferenceManager,
    private val assetRepository: AssetRepository,
    private val locationRepository: LocationRepository,
    private val attachmentRepository: AttachmentRepository
) : LiveCoroutinesViewModel() {
    val TAG = DetailWoViewModel::class.java.name

    private val _CurrentMember = MutableLiveData<Member>()
    private val _RequestRoute = MutableLiveData<ResponseRoute>()
    private val _MapsInfo = MutableLiveData<MapsLocation>()
    private val _AssetRfid = MutableLiveData<String>()
    private val _Result = MutableLiveData<Int>()
    private val _LocationRfid = MutableLiveData<String>()
    private val _ResultLocation = MutableLiveData<Int>()
    private val _Priority = MutableLiveData<Int>()


    val CurrentMember: LiveData<Member> get() = _CurrentMember
    val RequestRoute: LiveData<ResponseRoute> get() = _RequestRoute
    val MapsInfo: LiveData<MapsLocation> get() = _MapsInfo
    val AssetRfid: LiveData<String> get() = _AssetRfid
    val Result: LiveData<Int> get() = _Result
    val LocationRfid: LiveData<String> get() = _LocationRfid
    val ResultLocation: LiveData<Int> get() = _ResultLocation
    val Priority: LiveData<Int> get() = _Priority

    private lateinit var attachmentEntities: MutableList<AttachmentEntity>
    private var username: String? = null

    init {
        appSession.userEntity.let { userEntity ->
            if (userEntity.username != null) {
                username = userEntity.username
            }
        }
    }

    fun fetchWobyWonum(wonum: String) {
        val woCacheEntity: WoCacheEntity? = workOrderRepository.findWobyWonum(wonum)
        Timber.tag(TAG).d("viewmodel fetchWobyWonum() $wonum")
        Timber.tag(TAG).d("viewmodel fetchWobyWonum() $woCacheEntity")
        woCacheEntity.whatIfNotNull {
            val member = WoUtils.convertBodyToMember(woCacheEntity!!.syncBody.toString())
            _CurrentMember.value = member
        }
    }

    fun requestRoute(origin: String, destination: String) {
        Timber.tag(TAG).i("requestRoute() origin: %s destination: %s", origin, destination)
        viewModelScope.launch(Dispatchers.IO) {
            //Request Routing Map Base on Api Google Map
            googleMapsRepository.requestRoute(origin, destination, BaseParam.APP_APIKEY,
                onSuccess = {
                    Timber.tag(TAG).i("requestRoute() response: %s", it)
                    _RequestRoute.postValue(it)
                    getLocationInfo(it)
                },
                onError = {
                    Timber.tag(TAG).i("responseRoute() error: %s", it)
                })
        }
    }

    private fun getLocationInfo(dataDirection: ResponseRoute?) {
        val mapsLocation = MapsUtils.getLocationInfo(dataDirection)
        _MapsInfo.postValue(mapsLocation)
    }

    fun updateWo(
        woId: Int?,
        status: String?,
        wonum: String?,
        longdesc: String?,
        nextStatus: String
    ) {
        workOrderRepository.updateWoCacheBeforeSync(woId, wonum, status, longdesc, nextStatus)
        Timber.tag(TAG).d("updateWo() updateWoCacheBeforeSync()")

        val listMatAct = materialRepository.prepareMaterialActual(woId, wonum)

        val member = Member()
        member.status = nextStatus
        member.descriptionLongdescription = longdesc
        listMatAct.whatIfNotNullOrEmpty {
            member.matusetrans = it
        }

        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType: String = ("application/json")
        val patchType: String = BaseParam.APP_MERGE
        viewModelScope.launch(Dispatchers.IO) {
            if (woId != null) {
                workOrderRepository.updateStatus(cookie,
                    xMethodeOverride,
                    contentType,
                    patchType,
                    woId,
                    member,
                    onSuccess = {
                        workOrderRepository.updateWoCacheAfterSync(
                            woId,
                            wonum,
                            longdesc,
                            nextStatus
                        )
                        materialRepository.checkMatActAfterUpdate(woId)
                        saveScannerMaterial(woId)
                        uploadAttachments(woId)
                    },
                    onError = {
                        Timber.tag(TAG).i("updateWo() onError() onError: %s", it)
                    })
            }
        }
    }

    private fun saveScannerMaterial(woId: Int?) {
        val materialBackupList: List<MaterialBackupEntity?>? = woId?.let {
            materialRepository.listMaterialsByWoid(
                it
            )
        }
        for (i in materialBackupList!!.indices)
            materialBackupList[i]!!.workorderId = woId
        materialRepository.saveMaterialList(materialBackupList)
    }

    fun uploadAttachments(woId: Int) {
        attachmentEntities = attachmentRepository.getAttachmentByWoId(woId)
        Timber.tag(TAG)
            .d("uploadAttachments() woId: %s attachmentEntities: %s", woId, attachmentEntities)
        viewModelScope.launch(Dispatchers.IO) {
            username.whatIfNotNullOrEmpty { username ->
                attachmentRepository.uploadAttachment(attachmentEntities, username)
            }
        }
    }

    fun removeScanner(woId: Int): Long {
        return materialRepository.removeMaterialByWoid(woId)
    }

    fun removeAllWo() {
        return workOrderRepository.deleteWoEntity()
    }

    fun validateAsset(assetnum: String) {
        val assetEntity = assetRepository.findbyAssetnum(assetnum)
        assetEntity.whatIfNotNull {
            it.assetrfid.whatIfNotNull(
                whatIf = { rfidvalue ->
                    _AssetRfid.value = it.assetnum
                },
                whatIfNot = {
                    _AssetRfid.value = BaseParam.APP_DASH
                }
            )
        }
    }

    fun checkingResultAsset(result: Boolean) {
        if (result) {
            _Result.value = BaseParam.APP_TRUE
        } else {
            _Result.value = BaseParam.APP_FALSE
        }
    }

    fun validateLocation(location: String) {
        val locationEntity = locationRepository.findByLocation(location)
        locationEntity.whatIfNotNull {
            it.thisfsmrfid.whatIfNotNull(
                whatIf = { rfidvalue ->
                    Timber.d("validateLocation() %s", it.location)
                    _LocationRfid.value = it.location
                },
                whatIfNot = {
                    _LocationRfid.value = BaseParam.APP_DASH
                }
            )
        }
    }

    fun checkingResultLocation(result: Boolean) {
        if (result) {
            _ResultLocation.value = BaseParam.APP_TRUE
        } else {
            _ResultLocation.value = BaseParam.APP_FALSE
        }
    }

    fun compareResultScanner(originText: String, comapareText: String): Boolean {
        return originText.equals(comapareText)
    }

    fun setPriority(priority: Int) {
        _Priority.value = priority
    }

}