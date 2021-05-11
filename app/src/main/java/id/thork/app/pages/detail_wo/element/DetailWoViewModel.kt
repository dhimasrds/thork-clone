package id.thork.app.pages.detail_wo.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skydoves.whatif.whatIfNotNull
import com.squareup.moshi.Moshi
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
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
import java.util.*

/**
 * Created by M.Reza Sulaiman on 11/02/21
 * Jakarta, Indonesia.
 */
class DetailWoViewModel @ViewModelInject constructor(
    private val workOrderRepository: WorkOrderRepository,
    private val googleMapsRepository: GoogleMapsRepository,
    private val woActivityRepository: WoActivityRepository,
    private val materialRepository: MaterialRepository,
    private val appSession: AppSession
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

        updateWoCacheBeforeSync(woId, wonum, status, longdesc, nextStatus)

        val member = Member()
        member.status = nextStatus
        member.description_longdescription = longdesc


        val maxauth: String? = appSession.userHash
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType:String = ("application/json ")
        viewModelScope.launch(Dispatchers.IO) {
            if (woId != null) {
                workOrderRepository.updateStatus(maxauth!!,
                    xMethodeOverride,
                    contentType,
                    woId,
                    member,
                    onSuccess = {
                        updateWoCacheAfterSync(wonum, longdesc, nextStatus)
                        saveScannerMaterial(woId)
                        Timber.tag(TAG).i("onSuccess() success: %s", it)
                    },
                    onError = {
                        Timber.tag(TAG).i("onError() onError: %s", it)
                    })
            }
        }
    }

    private fun updateWoCacheBeforeSync(
        woId: Int?,
        wonum: String?,
        status: String?,
        longdesc: String?,
        nextStatus: String
    ) {
        val currentWoCache: WoCacheEntity? =
            wonum?.let {
                status?.let { woStatus ->
                    woActivityRepository.findWoByWonumAndStatus(
                        it,
                        woStatus
                    )
                }
            }

        val moshi = Moshi.Builder().build()
        val memberJsonAdapter = moshi.adapter(Member::class.java)
        val currentMember: Member? = memberJsonAdapter.fromJson(currentWoCache?.syncBody)
        currentMember?.status = nextStatus
        currentMember?.description_longdescription = longdesc

        if (currentWoCache?.isLatest == BaseParam.APP_TRUE) {
            currentWoCache.syncStatus = BaseParam.APP_FALSE
            currentWoCache.isLatest = BaseParam.APP_FALSE
            currentWoCache.let { workOrderRepository.updateWo(it, appSession.userEntity.username) }

            val newWoCache = WoCacheEntity()
            newWoCache.createdDate = Date()
            newWoCache.updatedDate = Date()
            newWoCache.createdBy = appSession.userEntity.username
            newWoCache.updatedBy = appSession.userEntity.username
            newWoCache.syncBody = memberJsonAdapter.toJson(currentMember)
            newWoCache.syncStatus = BaseParam.APP_FALSE
            newWoCache.isChanged = BaseParam.APP_TRUE
            newWoCache.isLatest = BaseParam.APP_TRUE
            newWoCache.laborCode = appSession.laborCode
            newWoCache.wonum = wonum
            newWoCache.status = nextStatus
            newWoCache.woId = woId
            workOrderRepository.saveWoList(newWoCache, appSession.userEntity.username)
        }


    }

    private fun updateWoCacheAfterSync(
        wonum: String?,
        longdesc: String?,
        nextStatus: String
    ) {
        val currentWoCache: WoCacheEntity? = wonum?.let {
            nextStatus.let { it1 ->
                woActivityRepository.findWoByWonumAndStatus(
                    it,
                    it1
                )
            }
        }

        val moshi = Moshi.Builder().build()
        val memberJsonAdapter = moshi.adapter(Member::class.java)
        val currentMember: Member? = memberJsonAdapter.fromJson(currentWoCache?.syncBody)
        currentMember?.status = nextStatus
        currentMember?.description_longdescription = longdesc

        currentWoCache?.syncBody = memberJsonAdapter.toJson(currentMember)
        currentWoCache?.syncStatus = BaseParam.APP_TRUE
        currentWoCache?.isChanged = BaseParam.APP_FALSE
        currentWoCache?.isLatest = BaseParam.APP_TRUE
        if (currentWoCache != null) {
            workOrderRepository.saveWoList(currentWoCache, appSession.userEntity.username)
        }
    }

    private fun saveScannerMaterial(woId: Int?){
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