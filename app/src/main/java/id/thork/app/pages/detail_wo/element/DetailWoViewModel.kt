package id.thork.app.pages.detail_wo.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.helper.MapsLocation
import id.thork.app.network.response.google_maps.ResponseRoute
import id.thork.app.network.response.work_order.Member
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.GoogleMapsRepository
import id.thork.app.repository.MaterialRepository
import id.thork.app.repository.WoActivityRepository
import id.thork.app.repository.WorkOrderRepository
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
) : LiveCoroutinesViewModel() {
    val TAG = DetailWoViewModel::class.java.name

    private val _CurrentMember = MutableLiveData<Member>()
    private val _RequestRoute = MutableLiveData<ResponseRoute>()
    private val _MapsInfo = MutableLiveData<MapsLocation>()

    val CurrentMember: LiveData<Member> get() = _CurrentMember
    val RequestRoute: LiveData<ResponseRoute> get() = _RequestRoute
    val MapsInfo: LiveData<MapsLocation> get() = _MapsInfo

    fun fetchWobyWonum(wonum: String) {
        val woCacheEntity: WoCacheEntity? = workOrderRepository.findWobyWonum(wonum)
        Timber.tag(TAG).d("viewmodel fetchWobyWonum() $woCacheEntity")
        woCacheEntity.whatIfNotNull {
            val member = WoUtils.convertBodyToMember(woCacheEntity!!.syncBody.toString())
            _CurrentMember.value = member
        }
    }

    fun requestRoute(origin: String, destination: String) {
        viewModelScope.launch(Dispatchers.IO) {
            //Request Routing Map Base on Api Google Map
            googleMapsRepository.requestRoute(origin, destination, BaseParam.APP_APIKEY,
                onSuccess = {
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

        val member = Member()
        member.status = nextStatus
        member.descriptionLongdescription = longdesc


        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType: String = ("application/json")
        viewModelScope.launch(Dispatchers.IO) {
            if (woId != null) {
                workOrderRepository.updateStatus(cookie,
                    xMethodeOverride,
                    contentType,
                    woId,
                    member,
                    onSuccess = {
                        workOrderRepository.updateWoCacheAfterSync(wonum, longdesc, nextStatus)
                        saveScannerMaterial(woId)
                        Timber.tag(TAG).i("onSuccess() success: %s", it)
                    },
                    onError = {
                        Timber.tag(TAG).i("onError() onError: %s", it)
                    })
            }
        }
    }

    private fun saveScannerMaterial(woId: Int?) {
        val materialList: List<MaterialEntity?>? = woId?.let {
            materialRepository.listMaterialsByWoid(
                it
            )
        }
        for (i in materialList!!.indices)
            materialList[i]!!.workorderId = woId
        materialRepository.saveMaterialList(materialList)
    }

    fun removeScanner(woId: Int): Long {
        return materialRepository.removeMaterialByWoid(woId)
    }

}