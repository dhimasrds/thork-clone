package id.thork.app.pages.create_wo.element

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
import id.thork.app.persistence.entity.*
import id.thork.app.repository.*
import id.thork.app.utils.DateUtils
import id.thork.app.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by Raka Putra on 3/15/21
 * Jakarta, Indonesia.
 */
class CreateWoViewModel @ViewModelInject constructor(
    private val materialRepository: MaterialRepository,
    private val appSession: AppSession,
    private val assetRepository: AssetRepository,
    private val locationRepository: LocationRepository,
    private val workOrderRepository: WorkOrderRepository,
    private val attachmentRepository: AttachmentRepository,
    private val taskRepository: TaskRepository,
    private val preferenceManager: PreferenceManager,
) : LiveCoroutinesViewModel() {
    private val TAG = CreateWoViewModel::class.java.name

    private var tempWonum: String? = null
    private var tempWoId: Int? = null

    private val _assetCache = MutableLiveData<AssetEntity>()
    private val _locationCache = MutableLiveData<LocationEntity>()

    val assetCache: LiveData<AssetEntity> get() = _assetCache
    val locationCache: LiveData<LocationEntity> get() = _locationCache

    private lateinit var attachmentEntities: MutableList<AttachmentEntity>
    private var username: String? = null

    init {
        appSession.userEntity.let { userEntity ->
            if (userEntity.username != null) {
                username = userEntity.username
            }
        }
    }

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
        estDur: Double?,
        workPriority: Int,
        longdesc: String?,
        tempWonum: String,
        tempWoId: Int,
        assetnum: String,
        location: String,
    ) {

//        val wsa = Woserviceaddres()
//        wsa.longitudex = longitudex
//        wsa.latitudey = latitudey
//        val woserviceaddress: MutableList<Woserviceaddres> = ArrayList<Woserviceaddres>()
//        woserviceaddress.add(wsa)

        val materialPlanlist = prepareMaterialTrans(tempWoId.toString())
        val taskList = taskRepository.prepareTaskBodyFromCreateWo(tempWoId)

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
        materialPlanlist.whatIfNotNullOrEmpty {
            member.wpmaterial = it
        }
        taskList.whatIfNotNullOrEmpty {
            member.woactivity = it
        }

        val moshi = Moshi.Builder().build()
        val memberJsonAdapter: JsonAdapter<Member> = moshi.adapter(Member::class.java)
        Timber.tag(TAG).d("createWorkOrderOnline() results: %s", memberJsonAdapter.toJson(member))

        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val properties = BaseParam.APP_ALL_PROPERTIES
        viewModelScope.launch(Dispatchers.IO) {
            workOrderRepository.createWo(
                cookie, properties, member,
                onSuccess = { woMember ->
                    woMember.woactivity.whatIfNotNullOrEmpty {
                        Timber.tag(TAG).i("updateToMaximo() onSuccess() onSuccess: %s", it)
                        taskRepository.handlingTaskSuccessFromCreateWo(woMember, it, tempWonum)
                    }
//                    uploadAttachments(tempWoId)
                }, onError = {
                    taskRepository.handlingTaskFailedFromCreateWo(tempWoId)
                    Timber.tag(TAG).i("createWo() error: %s", it)
                }
            )
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
        estDur: Double?, workPriority: Int, longdesc: String?, assetnum: String,
        location: String,
    ) {

//        val wsa = Woserviceaddres()
//        wsa.longitudex = longitudex
//        wsa.latitudey = latitudey
//        val woserviceaddress: MutableList<Woserviceaddres> = java.util.ArrayList<Woserviceaddres>()
//        woserviceaddress.add(wsa)

        tempWoId.whatIfNotNull { tempwoid ->
            val materialPlanlist = prepareMaterialTrans(tempWoId.toString())
            val taskList = taskRepository.prepareTaskBodyFromCreateWo(tempwoid)
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
            materialPlanlist.whatIfNotNullOrEmpty {
                member.wpmaterial = it
            }
            taskList.whatIfNotNullOrEmpty { tasklist ->
                member.woactivity = tasklist
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
            tWoCacheEntity.status = BaseParam.WAPPR
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

    fun removeTask(wonum: String): Long {
        return taskRepository.removeTaskByWonum(wonum)
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

    private fun prepareMaterialTrans(woid: String): List<Wpmaterial> {
        val materialPlanCache = materialRepository.getListMaterialPlanByWoid(woid)
        val listMaterialPlan = mutableListOf<Wpmaterial>()
        materialPlanCache.forEach {
            val wpmaterial = Wpmaterial()
            wpmaterial.itemnum = it.itemNum
            wpmaterial.itemqty = it.itemqty?.toDouble()
            wpmaterial.description = it.description
            wpmaterial.location = it.storeroom
            listMaterialPlan.add(wpmaterial)
        }
        return listMaterialPlan
    }
}