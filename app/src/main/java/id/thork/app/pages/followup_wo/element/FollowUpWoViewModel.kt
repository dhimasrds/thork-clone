package id.thork.app.pages.followup_wo.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skydoves.whatif.whatIfNotNull
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.work_order.Member
import id.thork.app.pages.create_wo.element.CreateWoViewModel
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.LocationEntity
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.AssetRepository
import id.thork.app.repository.LocationRepository
import id.thork.app.repository.MaterialRepository
import id.thork.app.repository.WorkOrderRepository
import id.thork.app.utils.DateUtils
import id.thork.app.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by Dhimas Saputra on 27/05/21
 * Jakarta, Indonesia.
 */
class FollowUpWoViewModel @ViewModelInject constructor(
    private val materialRepository: MaterialRepository,
    private val appSession: AppSession,
    private val assetRepository: AssetRepository,
    private val locationRepository: LocationRepository,
    private val workOrderRepository: WorkOrderRepository
) : LiveCoroutinesViewModel() {
    private val TAG = CreateWoViewModel::class.java.name

    private var tempWonum: String? = null
    private var tempWoId: Int? = null

    private val _assetCache = MutableLiveData<AssetEntity>()
    private val _locationCache = MutableLiveData<LocationEntity>()

    val assetCache: LiveData<AssetEntity> get() = _assetCache
    val locationCache: LiveData<LocationEntity> get() = _locationCache

    fun getTempWonum(): String? {
        if (tempWonum == null) {
            tempWonum = "WO-" + StringUtils.generateUUID()
            Timber.d("getTempWonum %s", tempWonum)
        }
        return tempWonum
    }

    fun getTempWoId(): Int? {
        if (tempWoId == null) {
            tempWoId = ThreadLocalRandom.current().nextInt()
            Timber.d("getTempWoId %s", tempWoId)
        }
        return tempWoId
    }

    fun estDuration(jam: Int, menit: Int): Double {
        val hasil: Double
        val hasilDetik: Int = jam * 3600 + menit * 60
        val hasilDetikDouble = hasilDetik.toDouble()
        hasil = hasilDetikDouble / 3600
        return hasil
    }

    fun createWorkOrderOnline(
        deskWo: String,
        estDur: Double?, workPriority: Int, longdesc: String?, tempWonum: String,
        assetnum: String,
        location: String
    ) {

//        val wsa = Woserviceaddres()
//        wsa.longitudex = longitudex
//        wsa.latitudey = latitudey
//        val woserviceaddress: MutableList<Woserviceaddres> = ArrayList<Woserviceaddres>()
//        woserviceaddress.add(wsa)

        val member = Member()
        member.siteid = appSession.siteId
        member.location = location
        member.assetnum = assetnum
        member.description = deskWo
        member.status = BaseParam.WAPPR
        member.reportdate = DateUtils.getDateTimeMaximo()
//        member.woserviceaddress = woserviceaddress
        member.estdur = estDur
        member.wopriority = workPriority
        member.descriptionLongdescription = longdesc

        val moshi = Moshi.Builder().build()
        val memberJsonAdapter: JsonAdapter<Member> = moshi.adapter(Member::class.java)
        Timber.tag(TAG).d("createWorkOrderOnline() results: %s", memberJsonAdapter.toJson(member))

        val maxauth: String? = appSession.userHash
        viewModelScope.launch(Dispatchers.IO) {
            workOrderRepository.createWo(
                maxauth!!, member,
                onSuccess = {

                }, onError = {
                    Timber.tag(TAG).i("createWo() error: %s", it)
                }
            )
        }
    }

    private fun saveScannerMaterial(tempWonum: String, woId: Int) {
        val materialList: List<MaterialEntity?>? =
            materialRepository.listMaterialsByWonum(tempWonum)
        for (i in materialList!!.indices)
            if (materialList[i]!!.workorderId == null) {
                materialList[i]!!.workorderId = woId
            }
        materialRepository.saveMaterialList(materialList)
    }

    fun createNewWoCache(
        deskWo: String,
        estDur: Double?, workPriority: Int, longdesc: String?, assetnum: String, location: String
    ) {
//        val wsa = Woserviceaddres()
//        wsa.longitudex = longitudex
//        wsa.latitudey = latitudey
//        val woserviceaddress: MutableList<Woserviceaddres> = java.util.ArrayList<Woserviceaddres>()
//        woserviceaddress.add(wsa)

        val member = Member()
        member.siteid = appSession.siteId
        member.location = location
        member.assetnum = assetnum
        member.description = deskWo
        member.status = BaseParam.WAPPR
        member.reportdate = DateUtils.getDateTimeMaximo()
//        member.woserviceaddress = woserviceaddress
        member.estdur = estDur
        member.wopriority = workPriority
        member.descriptionLongdescription = longdesc

        val tWoCacheEntity = WoCacheEntity()
        tWoCacheEntity.syncBody = convertToJson(member)
        tWoCacheEntity.syncStatus = BaseParam.APP_FALSE
        tWoCacheEntity.isChanged = BaseParam.APP_TRUE
        tWoCacheEntity.createdBy = appSession.userEntity.username
        tWoCacheEntity.updatedBy = appSession.userEntity.username
        tWoCacheEntity.createdDate = Date()
        tWoCacheEntity.updatedDate = Date()
        tWoCacheEntity.wonum = tempWonum
        tWoCacheEntity.status = BaseParam.WAPPR
        workOrderRepository.saveWoList(tWoCacheEntity, appSession.userEntity.username)
        Timber.d("createwointeractor: %s", longdesc)
    }

    private fun convertToJson(member: Member): String? {
        val gson = Gson()
        return gson.toJson(member)
    }

    fun removeScanner(wonum: String): Long {
        return materialRepository.removeMaterialByWonum(wonum)
    }

    fun checkResultAsset(assetnum: String) {
        val assetEntity = assetRepository.findbyAssetnum(assetnum)
        assetEntity.whatIfNotNull {
            _assetCache.value = it
        }
    }

    fun checkResultLocation(location: String) {
        val locationEntity = locationRepository.findByLocation(location)
        locationEntity.whatIfNotNull {
            _locationCache.value = it
        }
    }


}