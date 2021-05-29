package id.thork.app.repository

import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.material_response.Member
import id.thork.app.network.response.work_order.Wpmaterial
import id.thork.app.persistence.dao.MaterialBackupDao
import id.thork.app.persistence.dao.MaterialDao
import id.thork.app.persistence.dao.MatusetransDao
import id.thork.app.persistence.dao.WpmaterialDao
import id.thork.app.persistence.entity.MaterialBackupEntity
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.WpmaterialEntity
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

}