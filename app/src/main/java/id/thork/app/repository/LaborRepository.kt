package id.thork.app.repository

import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.labor_response.Laborcraftrate
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.Wplabor
import id.thork.app.persistence.dao.CraftMasterDao
import id.thork.app.persistence.dao.LaborActualDao
import id.thork.app.persistence.dao.LaborMasterDao
import id.thork.app.persistence.dao.LaborPlanDao
import id.thork.app.persistence.entity.CraftMasterEntity
import id.thork.app.persistence.entity.LaborActualEntity
import id.thork.app.persistence.entity.LaborMasterEntity
import id.thork.app.persistence.entity.LaborPlanEntity
import javax.inject.Inject

/**
 * Created by M.Reza Sulaiman on 29/07/2021
 * Jakarta, Indonesia.
 */
class LaborRepository @Inject constructor(
    appSession: AppSession,
    private val laborPlanDao: LaborPlanDao,
    private val laborActualDao: LaborActualDao,
    private val laborMasterDao: LaborMasterDao,
    private val craftMasterDao: CraftMasterDao
) : BaseRepository {
    val TAG = LaborRepository::class.java.name

    var usernameGlobal: String? = null

    /**
     * Labor Plan
     */

    fun saveLaborPlanCache(laborPlanEntity: LaborPlanEntity) {
        return laborPlanDao.createLaborPlanCache(laborPlanEntity, usernameGlobal)

    }

    fun removeLaborPlan() {
        return laborPlanDao.remove()
    }

    fun findListLaborplanWorkorderid(workroderid: String): List<LaborPlanEntity> {
        return laborPlanDao.findListLaborPlan(workroderid)
    }

    fun findLaborPlan(laborcode: String, workroderid: String): LaborPlanEntity? {
        return laborPlanDao.findlaborPlanByworkorderid(laborcode, workroderid)
    }

    fun findLaborPlanByLaborCodeCraft(laborcode: String, craft: String): LaborPlanEntity? {
        return laborPlanDao.findlaborPlanBylaborcodedandCraft(laborcode, craft)
    }

    fun findLaborPlanByCraft(craft: String, workroderid: String): LaborPlanEntity? {
        return laborPlanDao.findlaborPlanByworkorderidandCraft(craft, workroderid)
    }

    fun craftMaster(laborcode: String): List<CraftMasterEntity>? {
        return craftMasterDao.getListCraftByLaborcode(laborcode)
    }

    fun findLaborPlanbyTaskid(workroderid: String, taskid: String) : List<LaborPlanEntity>  {
        return laborPlanDao.findListLaborPlanlbyTaskid(workroderid, taskid)
    }


    fun addLaborPlanToObjectBox(member: Member) {
        val woact = member.woactivity
        woact.whatIfNotNullOrEmpty {
            it.forEach { task ->
                task.wplabor.whatIfNotNullOrEmpty { listLabor ->
                    listLabor.forEach {
                        val laborCache = LaborPlanEntity()
                        it.laborcode.whatIfNotNull { laborcode ->
                            laborCache.laborcode = laborcode
                            laborCache.taskid = task.taskid.toString()
                            laborCache.taskDescription = task.description
                        }

                        it.craft.whatIfNotNull { craft ->
                            laborCache.craft = craft
                            laborCache.skillLevel = it.skilllevel
                        }

                        it.vendor.whatIfNotNull { vendor ->
                            laborCache.vendor = vendor
                        }
                        laborCache.wplaborid = it.wplaborid.toString()
                        laborCache.wonumHeader = member.wonum
                        laborCache.workorderid = member.workorderid.toString()
                        laborCache.wonumTask = task.wonum
                        laborCache.syncUpdate = BaseParam.APP_TRUE
                        saveLaborPlanCache(laborCache)
                    }
                }
            }
        }
    }

    fun prepareBodyLaborPlan(workroderid: String, taskid: String) : List<Wplabor>{
        val listLaborPlan = laborPlanDao.findListLaborPlanlbyTaskid(workroderid, taskid)
        val wpLaborList = mutableListOf<Wplabor>()
        listLaborPlan.whatIfNotNull { listCache ->
            listCache.forEach { laborplan ->
                val wplabor = Wplabor()
                wplabor.wplaborid = 0
                wplabor.laborcode = laborplan.laborcode
                wpLaborList.add(wplabor)
            }
        }
        return wpLaborList
    }


    /**
     * Labor Actual
     */

    fun saveLaborActualCache(laborActualEntity: LaborActualEntity) {
        return laborActualDao.createLaborActualCache(laborActualEntity, usernameGlobal)
    }

    fun removeLaborActual() {
        return laborActualDao.remove()
    }

    fun findListLaborActualByWorkorderid(workroderid: String): List<LaborActualEntity> {
        return laborActualDao.findListLaborActual(workroderid)

    }

    fun findLaborActual(laborcode: String, workorderid: String): LaborActualEntity? {
        return laborActualDao.findlaborActualByworkorderid(laborcode, workorderid)
    }

    fun addLaborActualToObjectBox(member: Member) {
        val woactivity = member.woactivity
        woactivity.whatIfNotNullOrEmpty { it ->
            it.forEach { task ->
                task.labtrans.whatIfNotNullOrEmpty { listlabor ->
                    listlabor.forEach { labtran ->
                        val laborActual = LaborActualEntity()
                        laborActual.laborcode = labtran.laborcode
                        laborActual.labtransid = labtran.labtransid.toString()
                        laborActual.taskid = task.taskid.toString()
                        laborActual.taskDescription = task.description
                        laborActual.craft = labtran.craft

                        labtran.skilllevel.whatIfNotNull {
                            laborActual.skillLevel = it
                        }

                        labtran.vendor.whatIfNotNull {
                            laborActual.vendor = it
                        }

                        laborActual.wonumHeader = member.wonum
                        laborActual.workorderid = member.workorderid.toString()
                        laborActual.wonumTask = task.wonum
                        laborActual.syncUpdate = BaseParam.APP_TRUE

                        //TODO Adjustment time
//                        val finishdatetime = DateUtils.convertMaximoDateToMillisec(member.date)

                    }
                }
            }
        }
    }

    /**
     * Labor Master
     */

    fun saveLaborMasterCache(laborMasterEntity: LaborMasterEntity) {
        return laborMasterDao.createLaborMasterCache(laborMasterEntity, usernameGlobal)
    }

    fun removeLabormaster() {
        return laborMasterDao.remove()
    }

    fun fetchListLabor(): List<LaborMasterEntity> {
        return laborMasterDao.getListLaborMaster()
    }

    fun addLaborMasterCache(member: id.thork.app.network.response.labor_response.Member) {
        member.whatIfNotNull {
            val laborcode = it.laborcode.toString()
            val laborMaster = LaborMasterEntity()
            laborMaster.laborid = it.laborid.toString()
            laborMaster.laborcode = laborcode
            laborMaster.personid = it.personid
            laborMaster.worksite = it.worksite
            laborMaster.orgid = it.orgid
            laborMaster.status = it.status
            saveLaborMasterCache(laborMaster)
            //save craft
            it.laborcraftrate.whatIfNotNullOrEmpty {
                addCraftCache(it, laborcode)
            }
        }
    }

    /**
     * Craft Master
     */

    fun saveCraftCache(craftMasterEntity: CraftMasterEntity) {
        return craftMasterDao.createCraftCache(craftMasterEntity, usernameGlobal)
    }

    fun removeCraftMaster() {
        return craftMasterDao.remove()
    }

    fun getListCraftByLaborcode(laborcode: String): List<CraftMasterEntity> {
        return craftMasterDao.getListCraftByLaborcode(laborcode)
    }

    fun addCraftCache(listcraft: List<Laborcraftrate>, laborcode: String) {
        listcraft.forEach {
            val craftMasterEntity = CraftMasterEntity()
            craftMasterEntity.craft = it.craft
            craftMasterEntity.skillLevel = it.skilllevel
            craftMasterEntity.laborcode = laborcode
            saveCraftCache(craftMasterEntity)
        }
    }

    fun fetchCraft(laborcode: String): CraftMasterEntity? {
        return craftMasterDao.getCraftByLaborcode(laborcode)
    }

    fun fetchMasterCraft() : List<CraftMasterEntity>{
        return craftMasterDao.getCraft()
    }


    /**
     * save Master Data labor To Object Box
     */

    fun addMasterDataLaborToObjectBox(
        listMember: List<id.thork.app.network.response.labor_response.Member>,
        username: String
    ) {
        usernameGlobal = username
        listMember.forEach { member ->
            //save labor to local cache
            addLaborMasterCache(member)
        }
    }

    fun removeCacheLabor() {
        removeLabormaster()
        removeCraftMaster()
        removeLaborActual()
        removeLaborPlan()
    }
}