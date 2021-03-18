package id.thork.app.pages.create_wo.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.Woserviceaddres
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.MaterialRepository
import id.thork.app.repository.WorkOrderRepository
import id.thork.app.utils.DateUtils
import id.thork.app.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Raka Putra on 3/15/21
 * Jakarta, Indonesia.
 */
class CreateWoViewModel @ViewModelInject constructor(
    private val workOrderRepository: WorkOrderRepository,
    private val materialRepository: MaterialRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {
    private val TAG = CreateWoViewModel::class.java.name

    private var tempWonum: String? = null

    fun getTempWonum(): String? {
        if (tempWonum == null) {
            tempWonum = "WO-" + StringUtils.generateUUID()
            Timber.d("getTempWonum %s", tempWonum)
        }
        return tempWonum
    }

    fun estDuration(jam: Int, menit: Int): Double {
        val hasil: Double
        val hasilDetik: Int = jam * 3600 + menit * 60
        val hasilDetikDouble = hasilDetik.toDouble()
        hasil = hasilDetikDouble / 3600
        Timber.d("hasil detail : %s", hasilDetikDouble)
        Timber.d("hasil : %s", hasil)
        return hasil
    }

    fun createWorkOrderOnline(
        deskWo: String,
        longitudex: Double, latitudey: Double,
        estDur: Double, workPriority: Int, longdesc: String, tempWonum: String
    ) {

        val wsa = Woserviceaddres()
        wsa.longitudex = longitudex
        wsa.latitudey = latitudey
        val woserviceaddress: MutableList<Woserviceaddres> = ArrayList<Woserviceaddres>()
        woserviceaddress.add(wsa)

        val member = Member()
        member.siteid = appSession.siteId
        member.location = appSession.siteId
        member.description = deskWo
        member.status = BaseParam.WAPPR
        member.reportdate = DateUtils.getDateTimeMaximo()
        member.woserviceaddress = woserviceaddress
        member.estdur = estDur
        member.wopriority = workPriority
        member.description_longdescription = longdesc

        val moshi = Moshi.Builder().build()
        val memberJsonAdapter: JsonAdapter<Member> = moshi.adapter<Member>(Member::class.java)
        Timber.tag(TAG).d("createWorkOrderOnline() results: %s", memberJsonAdapter.toJson(member))

        val maxauth: String? = appSession.userHash
        val properties: String = ("workorderid,wonum")
        val contentType: String = ("application/json")
        viewModelScope.launch(Dispatchers.IO) {
            workOrderRepository.createWo(
                maxauth!!, properties, contentType, member,
                onSuccess = {
                    saveScannerMaterial(tempWonum, it.workorderid!!)
                    Timber.tag(TAG).i("createWo() success: %s", it)
                }, onError = {
                    Timber.tag(TAG).i("createWo() error: %s", it)
                }
            )
        }
    }

    private fun saveScannerMaterial(tempWonum: String, woId: Int){
        val materialList: List<MaterialEntity?>? = materialRepository.listMaterialsByWonum(tempWonum)
        for (i in materialList!!.indices)
            if (materialList[i]!!.workorderId == null){
                materialList[i]!!.workorderId = woId
            }
        materialRepository.saveMaterialList(materialList)
    }

    fun createNewWoCache(
        longitudex: Double, latitudey: Double, deskWo: String,
        estDur: Double, workPriority: Int, longdesc: String
    ) {
        Timber.d(
            "createNewWo() desc:%s, long:%s, lat:%s, estDur:%s, workPriority:%s, longdesc:%s",
            deskWo, longitudex, latitudey, estDur, workPriority, longdesc
        )
        val wsa = Woserviceaddres()
        wsa.longitudex = longitudex
        wsa.latitudey = latitudey
        val woserviceaddress: MutableList<Woserviceaddres> = java.util.ArrayList<Woserviceaddres>()
        woserviceaddress.add(wsa)

        val member = Member()
        member.siteid = appSession.siteId
        member.location = appSession.siteId
        member.description = deskWo
        member.status = BaseParam.WAPPR
        member.reportdate = DateUtils.getDateTimeMaximo()
        member.woserviceaddress = woserviceaddress
        member.estdur = estDur
        member.wopriority = workPriority
        member.description_longdescription = longdesc

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
}