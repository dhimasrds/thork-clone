package id.thork.app.repository

import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.material_response.Member
import id.thork.app.network.response.work_order.Matusetran
import id.thork.app.network.response.work_order.Wpmaterial
import id.thork.app.persistence.dao.MaterialBackupDao
import id.thork.app.persistence.dao.MaterialDao
import id.thork.app.persistence.dao.MatusetransDao
import id.thork.app.persistence.dao.WpmaterialDao
import id.thork.app.persistence.entity.MaterialBackupEntity
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.MatusetransEntity
import id.thork.app.persistence.entity.WpmaterialEntity
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Raka Putra on 3/4/21
 * Jakarta, Indonesia.
 */
class MaterialRepository @Inject constructor(
    private val materialBackupDao: MaterialBackupDao,
    private val matusetransDao: MatusetransDao,
    private val wpmaterialDao: WpmaterialDao,
    private val materialDao: MaterialDao,
    private val appSession: AppSession
) : BaseRepository {

    //TODO mateial Back Entitu
    fun saveMaterial(materialBackupEntity: MaterialBackupEntity): MaterialBackupEntity? {
        return materialBackupDao.saveMaterial(materialBackupEntity)
    }

    fun saveMaterialList(materialBackupEntity: List<MaterialBackupEntity?>): List<MaterialBackupEntity?>? {
        return materialBackupDao.saveMaterialList(materialBackupEntity)
    }

    fun listMaterials(workorderId: Int): List<MaterialBackupEntity?>? {
        return materialBackupDao.listMaterials(workorderId)
    }

    fun listMaterialsByWonum(wonum: String): List<MaterialBackupEntity?>? {
        return materialBackupDao.listMaterialsByWonum(wonum)
    }

    fun listMaterialsByWoid(woid: Int): List<MaterialBackupEntity?>? {
        return materialBackupDao.listMaterialsByWoid(woid)
    }

    fun removeMaterialByWonum(wonum: String): Long {
        return materialBackupDao.removeMaterialByWonum(wonum)
    }

    fun removeMaterialByWoid(woid: Int): Long {
        return materialBackupDao.removeMaterialByWoid(woid)
    }

    /**
     * Item Master
     */

    fun saveItemMaster(materialEntity: MaterialEntity, username: String) {
        materialDao.save(materialEntity, username)
    }

    fun saveListItemMaster(materialList: List<MaterialEntity>): List<MaterialEntity> {
        return materialDao.saveListMaterialMaster(materialList)
    }

    fun removeItemMaster() {
        return materialDao.remove()
    }

    fun getListItemMaster(): List<MaterialEntity>? {
        return materialDao.listMaterial()
    }

    fun getListByItemnum(itemnum: String): MaterialEntity? {
        return materialDao.findByItemnum(itemnum)
    }

    fun getListByTagcode(tagcode: String): MaterialEntity? {
        return materialDao.findBytagcode(tagcode)
    }

    fun addItemMasterToObjectBox(members: List<Member>) {
        removeItemMaster()
        val listMaterialcache = mutableListOf<MaterialEntity>()
        members.whatIfNotNull { listItemMaster ->
            listItemMaster.forEach {
                val materialEntity = MaterialEntity()
                materialEntity.itemNum = it.itemnum
                materialEntity.itemType = it.itemtype
                materialEntity.itemSetId = it.itemsetid
                materialEntity.description = it.description
                materialEntity.lotType = it.lottype
                materialEntity.lotTypeDescription = it.lottypeDescription
                it.thisfsmrfid.whatIfNotNull { tagrfid ->
                    materialEntity.thisfsmrfid = tagrfid
                }
                materialEntity.storeroom = it.inventory?.get(0)?.location
                listMaterialcache.add(materialEntity)
            }
            saveListItemMaster(listMaterialcache)
        }
    }

    /**
     * Material Plan
     */

    fun saveMaterialPlan(wpmaterialEntity: WpmaterialEntity, username: String) {
        wpmaterialDao.save(wpmaterialEntity, username)
    }

    fun saveListMaterialPlan(materialPlanList: List<WpmaterialEntity>): List<WpmaterialEntity> {
        return wpmaterialDao.saveListMaterialPlan(materialPlanList)
    }

    fun removeMaterialPlan() {
        return wpmaterialDao.remove()
    }

    fun getListMaterialPlanByWoid(workorderid: String): List<WpmaterialEntity> {
        return wpmaterialDao.findListMaterialActualByWoid(workorderid)
    }

    fun getMaterialPlanByWoidAndItemnum(workorderid: String, itemnum: String) : WpmaterialEntity? {
        return wpmaterialDao.findByWoidAndItemnum(workorderid, itemnum)
    }

    fun addListMaterialPlanToObjectBox(
        wpMaterialList: List<Wpmaterial>,
        wonum: String,
        workorderid: String
    ) {
        val templistMaterialPlan = mutableListOf<WpmaterialEntity>()
        wpMaterialList.whatIfNotNull { listMaterialPlan ->
            listMaterialPlan.forEach {
                val matPlanCache = WpmaterialEntity()
                matPlanCache.itemId = it.wpitemid
                matPlanCache.itemNum = it.itemnum
                matPlanCache.description = it.description
                matPlanCache.itemType = it.linetype
                matPlanCache.itemsetId = it.itemsetid
                matPlanCache.wonum = wonum
                matPlanCache.workorderId = workorderid
                matPlanCache.siteid = appSession.siteId
                matPlanCache.orgid = appSession.orgId
                matPlanCache.storeroom = it.location
                matPlanCache.itemqty = it.itemqty?.toInt()
                templistMaterialPlan.add(matPlanCache)
            }
            saveListMaterialPlan(templistMaterialPlan)
        }
    }

    fun addMaterialPlanToObjectBox(wpmaterial: Wpmaterial, wonum: String, workorderid: String) {
        val username = appSession.userEntity.username
        wpmaterial.whatIfNotNull {
            val matPlanCache = WpmaterialEntity()
            matPlanCache.itemId = it.wpitemid
            matPlanCache.itemNum = it.itemnum
            matPlanCache.description = it.description
            matPlanCache.itemType = it.linetype
            matPlanCache.itemsetId = it.itemsetid
            matPlanCache.wonum = wonum
            matPlanCache.workorderId = workorderid
            matPlanCache.siteid = appSession.siteId
            matPlanCache.orgid = appSession.orgId
            saveMaterialPlan(matPlanCache, username.toString())
        }
    }

    /**
     * Material Actual
     */

    fun saveMaterialActual(matusetransEntity: MatusetransEntity) {
        Timber.d("saveMaterialActual() %s", matusetransEntity.wonum)
        matusetransDao.save(matusetransEntity, appSession.userEntity.username.toString())
    }

    fun saveListMaterialActual(materialList: List<MatusetransEntity>) : List<MatusetransEntity> {
        return matusetransDao.saveListMaterialActual(materialList)
    }

    fun removeListMaterialActual() {
        return matusetransDao.remove()
    }

    fun getListMaterialActualByWoid(workorderid: String) : List<MatusetransEntity>? {
        return matusetransDao.findListMaterialActualByWoid(workorderid)
    }

    fun getListMaterialActualByWoidAndSyncUpdate(workorderid: String, syncUpdate: Int) : List<MatusetransEntity>? {
        return matusetransDao.findListMaterialActualByWoidAndSyncStatus(workorderid, syncUpdate)
    }


    fun getMaterialActualByWoid(workorderid: String, itemnum: String) : MatusetransEntity? {
        return matusetransDao.findByWoidAndItemnum(workorderid, itemnum)
    }

    fun removeMaterialActual(matusetransEntity: MatusetransEntity) {
        return matusetransDao.removeByEntity(matusetransEntity)
    }

    /**
     * perpare body
     */

    fun prepareMaterialActual(workorderid : Int?, wonum: String?) : List<Matusetran>? {
        val listMaterialCache = getListMaterialActualByWoidAndSyncUpdate(workorderid.toString(), BaseParam.APP_TRUE)
        val listMaterialBody = mutableListOf<Matusetran>()
        listMaterialCache.whatIfNotNullOrEmpty {
            it.forEach { entity ->
                val body = Matusetran()
                body.refwo = wonum
                body.storeloc = entity.storeroom
                body.linetype = BaseParam.ITEM
                body.itemnum = entity.itemNum
                body.issuetype = BaseParam.ISSUE
                entity.itemqty.whatIfNotNull { qty ->
                    body.quantity = -(qty.toDouble())
                }
                body.orgid = appSession.orgId
                body.tositeid = appSession.siteId
                listMaterialBody.add(body)
            }
            return listMaterialBody
        }
        return null
    }

    /**
     * Handling after Update
     */

    fun checkMatActAfterUpdate(workorderid: Int?) {
        val listMaterialExisting = getListMaterialActualByWoidAndSyncUpdate(workorderid.toString(), BaseParam.APP_TRUE)
        val tempListMaterial = mutableListOf<MatusetransEntity>()
        listMaterialExisting.whatIfNotNullOrEmpty {
            it.forEach {
                it.syncUpdate = BaseParam.APP_FALSE
                tempListMaterial.add(it)
            }
            saveListMaterialActual(tempListMaterial)
        }
    }


}