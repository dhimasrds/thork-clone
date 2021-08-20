package id.thork.app.repository

import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.labor_response.Laborcraftrate
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.Woactivity
import id.thork.app.network.response.work_order.Wplabor
import id.thork.app.persistence.dao.CraftMasterDao
import id.thork.app.persistence.dao.LaborActualDao
import id.thork.app.persistence.dao.LaborMasterDao
import id.thork.app.persistence.dao.LaborPlanDao
import id.thork.app.persistence.entity.CraftMasterEntity
import id.thork.app.persistence.entity.LaborActualEntity
import id.thork.app.persistence.entity.LaborMasterEntity
import id.thork.app.persistence.entity.LaborPlanEntity
import timber.log.Timber
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
) : BaseRepository() {
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

    fun findLaborPlanbyTaskid(workroderid: String, taskid: String): List<LaborPlanEntity> {
        return laborPlanDao.findListLaborPlanlbyTaskid(workroderid, taskid)
    }

    fun findLaborPlanByWplaborid(wplaborid: String): LaborPlanEntity? {
        return laborPlanDao.findlaborPlanByWplaborid(wplaborid)
    }


    fun addLaborPlanToObjectBox(member: Member) {
        //Labor Plan with Task
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
                        laborCache.isTask = BaseParam.APP_TRUE
                        saveLaborPlanCache(laborCache)
                    }
                }
            }
        }

        //Labor plan without task
        val wplabor = member.wplabor
        wplabor.whatIfNotNullOrEmpty {
            it.forEach { wplabor ->
                val laborCache = LaborPlanEntity()
                wplabor.laborcode.whatIfNotNull { laborcode ->
                    laborCache.laborcode = laborcode
                }

                wplabor.craft.whatIfNotNull { craft ->
                    laborCache.craft = craft
                }

                wplabor.vendor.whatIfNotNull { vendor ->
                    laborCache.vendor = vendor
                }
                laborCache.wplaborid = wplabor.wplaborid.toString()
                laborCache.wonumHeader = member.wonum
                laborCache.workorderid = member.workorderid.toString()
                laborCache.syncUpdate = BaseParam.APP_TRUE
                laborCache.isTask = BaseParam.APP_FALSE
                saveLaborPlanCache(laborCache)
            }
        }
    }

    //create labor plan without task
    fun prepareBodyLaborPlan(workroderid: String): List<Wplabor> {
        val listLaborPlan = laborPlanDao.findListLaborPlan(workroderid)
        val wpLaborList = mutableListOf<Wplabor>()
        //checking taskid
        listLaborPlan.whatIfNotNull { listCache ->
            Timber.tag(TAG).d("prepareBodyLaborPlan() listCache size %s", listCache.size)
            listCache.forEach { laborplan ->
                if (laborplan.isTask == BaseParam.APP_FALSE && laborplan.syncUpdate == BaseParam.APP_FALSE) {
                    val wplabor = Wplabor()
                    wplabor.wplaborid = 0
                    laborplan.laborcode.whatIfNotNull(
                        whatIf = {
                            if (it != BaseParam.APP_DASH) {
                                wplabor.laborcode = it
                            } else {
                                wplabor.craft = laborplan.craft
                            }
                        },
                        whatIfNot = {
                            wplabor.craft = laborplan.craft
                        }
                    )

                    wpLaborList.add(wplabor)
                }
            }
        }
        return wpLaborList
    }

    //Update labor plan without task
    fun preapreBodyLaborPlanNontask(
        wplaborid: String,
        laborcode: String,
        craft: String
    ): List<Wplabor> {
        val laborplanEntity = laborPlanDao.findlaborPlanByWplaborid(wplaborid)
        val wpLaborList = mutableListOf<Wplabor>()
        laborplanEntity.whatIfNotNull { cache ->
            val wplabor = Wplabor()
            wplabor.wplaborid = cache.wplaborid?.toInt()

            if (laborcode != BaseParam.APP_DASH) {
                wplabor.laborcode = laborcode
            } else {
                wplabor.craft = craft
            }

            wpLaborList.add(wplabor)
        }
        return wpLaborList
    }


    //Update labor plan with task
    fun preapreBodyLaborPlanTask(
        wplaborid: String,
        taskid: String,
        wonumtask: String
    ): List<Woactivity> {
        val woactivityList = mutableListOf<Woactivity>()
        val listWplabor = mutableListOf<Wplabor>()
        val laborplanEntity = laborPlanDao.findlaborPlanByWplaborid(wplaborid)

        laborplanEntity.whatIfNotNull {
            val wplabor = Wplabor()
            wplabor.wplaborid = it.wplaborid?.toInt()
            if (it.laborcode != BaseParam.APP_DASH) {
                wplabor.laborcode = it.laborcode
            } else {
                wplabor.craft = it.craft
            }
            listWplabor.add(wplabor)
        }

        listWplabor.whatIfNotNullOrEmpty {
            val woactivity = Woactivity()
            woactivity.taskid = taskid.toInt()
            woactivity.wonum = wonumtask
            woactivity.wplabor = it
            woactivityList.add(woactivity)
        }

        return woactivityList
    }


    fun handlingLaborPlan(
        wpLaborList: List<Wplabor>,
        member: Member,
        tempwoid: String
    ) {
        val wonumheader = member.wonum
        val woidHeader = member.workorderid
        Timber.tag(TAG).d("handlingLaborPlan() list size : %s", wpLaborList.size)
        wpLaborList.whatIfNotNullOrEmpty { wplaborlist ->
            wplaborlist.forEach { wpLabor ->
                val laborcode = wpLabor.laborcode
                Timber.tag(TAG).d("handlingLaborPlan() Laborcode %s", wpLabor.laborcode)
                laborcode.whatIfNotNull(
                    whatIf = {
                        val laborCache = laborPlanDao.findlaborPlanByworkorderid(
                            laborcode.toString(),
                            tempwoid
                        )
                        Timber.tag(TAG).d("handlingLaborPlan() laborcache %s", laborCache)
                        laborCache.whatIfNotNull { cache ->
                            val wplaborid = wpLabor.wplaborid.toString()
                            updateLaborPlanCache(
                                cache,
                                wplaborid,
                                wonumheader.toString(),
                                woidHeader.toString(),
                            )
                        }
                    },
                    whatIfNot = {
                        val laborCache = laborPlanDao.findlaborPlanByworkorderidandCraft(
                            wpLabor.craft.toString(),
                            tempwoid
                        )
                        Timber.tag(TAG).d("handlingLaborPlan() laborcode not avail %s", laborCache)
                        laborCache.whatIfNotNull { cache ->
                            val wplaborid = wpLabor.wplaborid.toString()
                            Timber.tag(TAG).d("handlingLaborPlan() save to local")
                            updateLaborPlanCache(
                                cache,
                                wplaborid,
                                wonumheader.toString(),
                                woidHeader.toString(),
                            )
                        }
                    }
                )
            }
        }
    }

    private fun updateLaborPlanCache(
        laborPlanEntity: LaborPlanEntity,
        wplaborid: String,
        wonumheader: String,
        woidHeader: String
    ) {
        laborPlanEntity.wplaborid = wplaborid
        laborPlanEntity.wonumHeader = wonumheader
        laborPlanEntity.workorderid = woidHeader
        laborPlanEntity.syncUpdate = BaseParam.APP_TRUE
        laborPlanDao.createLaborPlanCache(laborPlanEntity, usernameGlobal)
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
            it.laborcraftrate.whatIfNotNullOrEmpty { laborCraftRate ->
                addCraftCache(laborCraftRate, laborcode)
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
            Timber.tag(TAG).d(
                "fetchMasterDataLabor() addCraftCache laborcode %s, craft %s",
                laborcode,
                it.craft
            )
            craftMasterEntity.craft = it.craft
            craftMasterEntity.skillLevel = it.skilllevel
            craftMasterEntity.laborcode = laborcode
            saveCraftCache(craftMasterEntity)
        }
    }

    fun fetchCraft(laborcode: String): CraftMasterEntity? {
        return craftMasterDao.getCraftByLaborcode(laborcode)
    }

    fun fetchMasterCraft(): Array<out String>? {
        return craftMasterDao.getCraft()
    }


    /**
     * save Master Data labor To Object Box
     */

    fun addMasterDataLaborToObjectBox(
        listMember: List<id.thork.app.network.response.labor_response.Member>,
        username: String
    ) {
        removeCacheLabor()
        usernameGlobal = username
        Timber.tag(TAG).d("fetchMasterDataLabor() size %s", listMember.size)
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