package id.thork.app.pages.followup_wo.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.Wpmaterial
import id.thork.app.pages.create_wo.element.CreateWoViewModel
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.LocationEntity
import id.thork.app.persistence.entity.MaterialBackupEntity
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.*
import id.thork.app.utils.DateUtils
import id.thork.app.utils.StringUtils
import id.thork.app.utils.WoUtils
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
    private val workOrderRepository: WorkOrderRepository,
    private val preferenceManager: PreferenceManager,
    private val taskRepository: TaskRepository,
    private val laborRepository: LaborRepository
) : LiveCoroutinesViewModel() {
    private val TAG = CreateWoViewModel::class.java.name

    private var tempWonum: String? = null
    private var tempWoId: Int? = null

    private val _assetCache = MutableLiveData<AssetEntity>()
    private val _locationCache = MutableLiveData<LocationEntity>()
    private val _updateSucces = MutableLiveData<Int>()


    val assetCache: LiveData<AssetEntity> get() = _assetCache
    val locationCache: LiveData<LocationEntity> get() = _locationCache
    val updateSucces: LiveData<Int> get() = _updateSucces


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

    fun createFollowUpWorkOrderOnline(
        deskWo: String,
        estDur: Double?,
        workPriority: Int,
        longdesc: String?,
        tempWonum: String,
        assetnum: String,
        location: String,
        origwonum: String,
        tempWoid: String
    ) {

//        val wsa = Woserviceaddres()
//        wsa.longitudex = longitudex
//        wsa.latitudey = latitudey
//        val woserviceaddress: MutableList<Woserviceaddres> = ArrayList<Woserviceaddres>()
//        woserviceaddress.add(wsa)
        tempWoId.whatIfNotNull { tempwoid ->
            val externalrefid = WoUtils.getExternalRefid()
            val materialPlanlist = prepareMaterialTrans(tempWoId.toString())
            val taskList = taskRepository.prepareTaskBodyFromCreateWo(tempwoid)
            val laborplanList = laborRepository.prepareBodyLaborPlan(tempWoId.toString())

            val member = Member()
            member.siteid = appSession.siteId
            if (!location.equals(BaseParam.APP_DASH)) {
                member.location = location
            }

            if (!assetnum.equals(BaseParam.APP_DASH)) {
                member.assetnum = assetnum
            }
            member.description = deskWo
            member.status = BaseParam.WAPPR
            member.reportdate = DateUtils.getDateTimeMaximo()
//        member.woserviceaddress = woserviceaddress
            member.estdur = estDur
            member.wopriority = workPriority
            member.descriptionLongdescription = longdesc
            origwonum.whatIfNotNull {
                member.origrecordid = it
                member.origrecordclass = BaseParam.WORKORDER
            }

            materialPlanlist.whatIfNotNullOrEmpty {
                member.wpmaterial = it
            }
            taskList.whatIfNotNullOrEmpty {
                member.woactivity = it
            }

            externalrefid.whatIfNotNull {
                member.externalrefid = it
            }

            laborplanList.whatIfNotNullOrEmpty {
                member.wplabor = it
            }

            //save create Wo to local
            workOrderRepository.saveCreatedWoLocally(
                member,
                tempWonum,
                externalrefid
            )

            val moshi = Moshi.Builder().build()
            val memberJsonAdapter: JsonAdapter<Member> = moshi.adapter(Member::class.java)
            Timber.tag(TAG)
                .d("createFollowUpWorkOrderOnline() results: %s", memberJsonAdapter.toJson(member))

            val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
            val properties = BaseParam.APP_ALL_PROPERTIES
            viewModelScope.launch(Dispatchers.IO) {
                workOrderRepository.createWo(
                    cookie, properties, member,
                    onSuccess = { woMember ->
                        val workorderid = woMember.workorderid
                        val wonum = woMember.wonum
                        workOrderRepository.updateCreateWoCacheOnlineMode(
                            workorderid,
                            wonum,
                            tempWonum,
                            woMember
                        )
                        woMember.woactivity.whatIfNotNullOrEmpty {
                            Timber.tag(TAG).i("createFollowUpWorkOrderOnline() onSuccess() onSuccess: %s", it)
                            taskRepository.handlingTaskSuccessFromCreateWo(woMember, it, tempWonum)
                        }

                        woMember.wplabor.whatIfNotNullOrEmpty {
                            Timber.tag(TAG).i("createFollowUpWorkOrderOnline() list wp labor %s", it.size)
                            laborRepository.handlingLaborPlan(it, woMember, tempWoId.toString())
                        }
                        _updateSucces.postValue(BaseParam.APP_TRUE)

                    }, onError = {
                        taskRepository.handlingTaskFailedFromCreateWo(tempwoid)
                        Timber.tag(TAG).i("createFollowUpWorkOrderOnline() error: %s", it)
                    }
                )
            }
        }
    }

    private fun saveScannerMaterial(tempWonum: String, woId: Int) {
        val materialBackupList: List<MaterialBackupEntity?>? =
            materialRepository.listMaterialsByWonum(tempWonum)
        for (i in materialBackupList!!.indices)
            if (materialBackupList[i]!!.workorderId == null) {
                materialBackupList[i]!!.workorderId = woId
            }
        materialRepository.saveMaterialList(materialBackupList)
    }

    fun createNewWoCache(
        deskWo: String,
        estDur: Double?,
        workPriority: Int,
        longdesc: String?,
        assetnum: String,
        location: String,
        origwonum: String,
        tempWoid: String
    ) {
//        val wsa = Woserviceaddres()
//        wsa.longitudex = longitudex
//        wsa.latitudey = latitudey
//        val woserviceaddress: MutableList<Woserviceaddres> = java.util.ArrayList<Woserviceaddres>()
//        woserviceaddress.add(wsa)
        tempWoId.whatIfNotNull { tempwoid ->
            val materialPlanlist = prepareMaterialTrans(tempWoId.toString())
            val taskList = taskRepository.prepareTaskBodyFromCreateWo(tempwoid)
            val laborplanList = laborRepository.prepareBodyLaborPlan(tempwoid.toString())
            val member = Member()
            member.siteid = appSession.siteId
            if (!location.equals(BaseParam.APP_DASH)) {
                member.location = location
            }

            if (!assetnum.equals(BaseParam.APP_DASH)) {
                member.assetnum = assetnum
            }
            member.wonum = tempWonum
            member.workorderid = tempwoid
            member.description = deskWo
            member.status = BaseParam.WAPPR
            member.reportdate = DateUtils.getDateTimeMaximo()
//        member.woserviceaddress = woserviceaddress
            member.estdur = estDur
            member.wopriority = workPriority
            member.descriptionLongdescription = longdesc
            origwonum.whatIfNotNull {
                member.origrecordid = it
                member.origrecordclass = BaseParam.WORKORDER
            }
            materialPlanlist.whatIfNotNullOrEmpty {
                member.wpmaterial = it
            }
            taskList.whatIfNotNullOrEmpty { tasklist ->
                member.woactivity = tasklist
            }

            laborplanList.whatIfNotNullOrEmpty { wplabor ->
                member.wplabor = wplabor

            }

            val tWoCacheEntity = WoCacheEntity()
            tWoCacheEntity.syncBody = convertToJson(member)
            tWoCacheEntity.syncStatus = BaseParam.APP_FALSE
            tWoCacheEntity.isChanged = BaseParam.APP_TRUE
            tWoCacheEntity.createdBy = appSession.userEntity.username
            tWoCacheEntity.updatedBy = appSession.userEntity.username
            tWoCacheEntity.createdDate = Date()
            tWoCacheEntity.updatedDate = Date()
            tWoCacheEntity.wonum = tempWonum
            tWoCacheEntity.woId = tempWoId
            tWoCacheEntity.status = BaseParam.WAPPR
            tWoCacheEntity.externalREFID = WoUtils.getExternalRefid()
            workOrderRepository.saveWoList(tWoCacheEntity, appSession.userEntity.username)
            Timber.d("createwointeractor: %s", longdesc)
        }
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

    private fun prepareMaterialTrans(woid: String): List<Wpmaterial> {
        val materialPlanCache = materialRepository.getListMaterialPlanByWoid(woid.toInt())
        val listMaterialPlan = mutableListOf<Wpmaterial>()
        materialPlanCache.forEach {
            val wpmaterial = Wpmaterial()
            wpmaterial.itemnum = it.itemNum
            wpmaterial.itemqty = it.itemQty?.toDouble()
            wpmaterial.description = it.description
            wpmaterial.location = it.storeroom
            listMaterialPlan.add(wpmaterial)
        }
        return listMaterialPlan
    }

    fun removeTask(wonum: String): Long {
        return taskRepository.removeTaskByWonum(wonum)
    }
}