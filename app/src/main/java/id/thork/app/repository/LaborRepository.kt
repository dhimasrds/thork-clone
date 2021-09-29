package id.thork.app.repository

import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.labor_response.Laborcraftrate
import id.thork.app.network.response.work_order.Labtran
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
import id.thork.app.utils.DateUtils
import id.thork.app.utils.StringUtils
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

    fun removeLaborPlanByEntity(laborPlanEntity: LaborPlanEntity) {
        return laborPlanDao.removeByEntity(laborPlanEntity)
    }

    fun removeLaborActualByEntity(laborActualEntity: LaborActualEntity) {
        return laborActualDao.removeLaborActualByEntity(laborActualEntity)
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

    fun findlaborPlanByObjectboxId(objectboxid: Long): LaborPlanEntity? {
        return laborPlanDao.findlaborPlanByObjectBoxid(objectboxid)
    }

    fun findlaborActualByObjectboxId(objectboxid: Long): LaborActualEntity? {
        return laborActualDao.findlaborActualByObjectBoxid(objectboxid)
    }

    fun findListLaborPlanBySyncUpdateAndLocally(
        syncupdate: Int,
        isLocally: Int
    ): List<LaborPlanEntity> {
        return laborPlanDao.findListLaborPlanlbySyncUpdateAndisLocally(syncupdate, isLocally)
    }

    fun addLaborPlanToObjectBox(member: Member) {
        //Labor Plan with Task
        val woact = member.woactivity
        woact.whatIfNotNullOrEmpty {
            it.forEach { task ->
                task.wplabor.whatIfNotNullOrEmpty { listLabor ->
                    listLabor.forEach { wplabor ->
                        val laborCache = LaborPlanEntity()
                        wplabor.laborcode.whatIfNotNull { laborcode ->
                            laborCache.laborcode = laborcode
                            laborCache.taskid = task.taskid.toString()
                            laborCache.taskDescription = task.description
                        }

                        wplabor.craft.whatIfNotNull { craft ->
                            laborCache.craft = craft
                            laborCache.skillLevel = wplabor.skilllevel
                        }

                        wplabor.vendor.whatIfNotNull { vendor ->
                            laborCache.vendor = vendor
                        }
                        laborCache.wplaborid = wplabor.wplaborid.toString()
                        laborCache.wonumHeader = member.wonum
                        laborCache.workorderid = member.workorderid.toString()
                        laborCache.wonumTask = task.wonum
                        laborCache.syncUpdate = BaseParam.APP_TRUE
                        laborCache.isTask = BaseParam.APP_TRUE
                        laborCache.isLocally = BaseParam.APP_TRUE
                        wplabor.localref.whatIfNotNull { localref ->
                            laborCache.localRef = StringUtils.subStringLocalref(localref)
                        }
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
                laborCache.isLocally = BaseParam.APP_TRUE
                wplabor.localref.whatIfNotNull { localref ->
                    laborCache.localRef = StringUtils.subStringLocalref(localref)
                }
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

    //Prepare Body labor plan for offline mode
    fun prepareBodyLaborPlanOfflinemode(laborPlanEntity: LaborPlanEntity): List<Wplabor> {
        val wpLaborList = mutableListOf<Wplabor>()
        val wplabor = Wplabor()
        wplabor.wplaborid = 0
        laborPlanEntity.laborcode.whatIfNotNull(
            whatIf = {
                if (it != BaseParam.APP_DASH) {
                    wplabor.laborcode = it
                } else {
                    wplabor.craft = laborPlanEntity.craft
                }
            },
            whatIfNot = {
                wplabor.craft = laborPlanEntity.craft
            }
        )
        wpLaborList.add(wplabor)
        return wpLaborList
    }

    //Prepare Body update Labor plan to maximo
    fun prepareBodyUpdateLaborPlanTomaximo(laborPlanEntity: LaborPlanEntity): Member {
        //need prepare body, and validation with task or without task
        val wplaborid = laborPlanEntity.wplaborid.toString()
        val taskid = laborPlanEntity.taskid.toString()
        val wonumTask = laborPlanEntity.wonumTask.toString()
        val laborCode = laborPlanEntity.laborcode.toString()
        val craft = laborPlanEntity.craft.toString()
        val prepareBody = Member()

        if (laborPlanEntity.isTask == BaseParam.APP_TRUE) {
            //Prepare body with task
            val bodyLaborplanWithTask =
                preapreBodyLaborPlanTask(wplaborid, taskid, wonumTask)
            bodyLaborplanWithTask.whatIfNotNullOrEmpty {
                prepareBody.woactivity = it
            }
        } else {
            //Prepare body without task
            val bodyLaborplanWithoutTask =
                preapreBodyLaborPlanNontask(wplaborid, laborCode, craft)
            bodyLaborplanWithoutTask.whatIfNotNullOrEmpty {
                prepareBody.wplabor = it
            }
        }
        return prepareBody
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

    //Handling labor plan after update success
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
                val localref = wpLabor.localref
                Timber.tag(TAG)
                    .d("handlingLaborPlan() Laborcode %s woid %s", wpLabor.laborcode, tempwoid)
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
                                localref.toString()
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
                                localref.toString()
                            )
                        }
                    }
                )
            }
        }
    }

    //Update labor plan when update success
    private fun updateLaborPlanCache(
        laborPlanEntity: LaborPlanEntity,
        wplaborid: String,
        wonumheader: String,
        woidHeader: String,
        localref: String
    ) {
        laborPlanEntity.wplaborid = wplaborid
        laborPlanEntity.wonumHeader = wonumheader
        laborPlanEntity.workorderid = woidHeader
        laborPlanEntity.syncUpdate = BaseParam.APP_TRUE
        laborPlanEntity.localRef = StringUtils.subStringLocalref(localref)
        laborPlanDao.createLaborPlanCache(laborPlanEntity, usernameGlobal)
    }

    //Handling creaete labor plan cache when update success
    fun handlingCreateLaborPlan(member: Member, laborPlanEntity: LaborPlanEntity) {
        val isTask = laborPlanEntity.isTask
        val listLpTask = member.woactivity
        val listLpNonTask = member.wplabor

        if (isTask == BaseParam.APP_TRUE) {
            listLpTask.whatIfNotNullOrEmpty { list ->
                list.forEach {
                    it.wplabor.whatIfNotNullOrEmpty { wplabors ->
                        wplabors.forEach { labor ->
                            laborPlanEntity.wonumTask = it.wonum
                            checkingLaborPlanExisting(
                                labor,
                                laborPlanEntity
                            )
                        }
                    }
                }
            }
        } else {
            listLpNonTask.whatIfNotNullOrEmpty {
                it.forEach { wplabor ->
                    checkingLaborPlanExisting(wplabor, laborPlanEntity)
                }
            }
        }
    }

    //handling labor plan cache when update labor plan to mx success
    fun handlingUpdateLaborPlan(laborPlanEntity: LaborPlanEntity, syncUpdate: Int, isLocally: Int) {
        laborPlanEntity.syncUpdate = syncUpdate
        laborPlanEntity.isLocally = isLocally
        saveLaborPlanCache(laborPlanEntity)
    }

    //Checking labor plan existing to replace wplaborid
    private fun checkingLaborPlanExisting(wplabor: Wplabor, laborPlanEntity: LaborPlanEntity) {
        val wplaborid = wplabor.wplaborid.toString()
        val localref = wplabor.localref.toString()
        val cacheLaborPlan = findLaborPlanByWplaborid(wplaborid)
        if (cacheLaborPlan == null) {
            Timber.tag(TAG).i("checkingLaborPlanExisting() update local cache after update")
            laborPlanEntity.wplaborid = wplaborid
            laborPlanEntity.syncUpdate = BaseParam.APP_TRUE
            laborPlanEntity.isLocally = BaseParam.APP_TRUE
            laborPlanEntity.localRef = StringUtils.subStringLocalref(localref)
            saveLaborPlanCache(laborPlanEntity)
        }
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

    fun findLaborActualByLabtransid(labtransid: String): LaborActualEntity? {
        return laborActualDao.findlaborActualByLabtransid(labtransid)
    }

    fun findLaborActual(laborcode: String, workorderid: String): LaborActualEntity? {
        return laborActualDao.findlaborActualByworkorderid(laborcode, workorderid)
    }

    fun findListLaborActualBySyncUpdateAndLocally(
        syncupdate: Int,
        isLocally: Int
    ): List<LaborActualEntity> {
        return laborActualDao.findListLaborActualSyncUpdateAndLocally(syncupdate, isLocally)
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
                        laborActual.isTask = BaseParam.APP_TRUE

                        val startdatetimemx = labtran.startdatetime
                        val finishdatetimemx = labtran.finishdatetime

                        startdatetimemx.whatIfNotNull {
                            laborActual.startDateForMaximo = it
                            laborActual.startDate = DateUtils.convertMaximoDateToDate(it)
                            laborActual.statTime = DateUtils.convertMaximoDateToTime(it)
                        }

                        finishdatetimemx.whatIfNotNull {
                            laborActual.endDateForMaximo = it
                            laborActual.endDate = DateUtils.convertMaximoDateToDate(it)
                            laborActual.endTime = DateUtils.convertMaximoDateToTime(it)
                        }

                        saveLaborActualCache(laborActual)
                    }
                }
            }
        }

        val wolabtran = member.labtrans
        wolabtran.whatIfNotNullOrEmpty { labtrans ->
            labtrans.forEach { labact ->
                val laborActual = LaborActualEntity()
                laborActual.laborcode = labact.laborcode
                laborActual.labtransid = labact.labtransid.toString()
                laborActual.craft = labact.craft

                labact.skilllevel.whatIfNotNull {
                    laborActual.skillLevel = it
                }

                labact.vendor.whatIfNotNull {
                    laborActual.vendor = it
                }

                laborActual.wonumHeader = member.wonum
                laborActual.workorderid = member.workorderid.toString()
                laborActual.syncUpdate = BaseParam.APP_TRUE
                laborActual.isTask = BaseParam.APP_FALSE
                saveLaborActualCache(laborActual)

            }

        }
    }

    //Update labor actual with task
    fun preapreBodyLaborActualTask(
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

    fun prepareBodyLaborActualwithTask(workroderid: String, taskid: String): List<Labtran> {
        val listLaborActual = laborActualDao.findListLaborActualbyTaskid(workroderid, taskid)
        val labtransList = mutableListOf<Labtran>()
        listLaborActual.whatIfNotNullOrEmpty { listCache ->
            listCache.forEach { laboractual ->
                if (laboractual.syncUpdate == BaseParam.APP_FALSE) {
                    val labtran = Labtran()
                    laboractual.laborcode.whatIfNotNull(
                        whatIf = {
                            labtran.labtransid = 0
                            labtran.laborcode = it
                        }
                    )
                    labtransList.add(labtran)
                }
            }
        }
        return labtransList
    }

    fun prepareBodyLaborActualNonTask(workroderid: String) : List<Labtran> {
        val listLaborActual = laborActualDao.findListLaborActual(workroderid)
        val labtransList = mutableListOf<Labtran>()

        listLaborActual.whatIfNotNullOrEmpty { list ->
            list.forEach { laboractual ->
                if(laboractual.syncUpdate == BaseParam.APP_FALSE && laboractual.isTask == BaseParam.APP_FALSE) {
                    val labtran = Labtran()
                    laboractual.laborcode.whatIfNotNull {
                        labtran.labtransid = 0
                        labtran.laborcode = it
                        labtran.startdatetime = laboractual.startDateForMaximo
                        labtransList.add(labtran)
                    }
                }
            }
        }
        return labtransList
    }

    fun prepareBodyLaborActualNonTaskOfflineMode(laborActualEntity: LaborActualEntity) : List<Labtran> {
        val labtransList = mutableListOf<Labtran>()
        val labtran = Labtran()
        laborActualEntity.whatIfNotNull {
            labtran.labtransid = 0
            labtran.laborcode = it.laborcode
            labtran.startdatetime = it.startDateForMaximo
            labtransList.add(labtran)
        }

        return labtransList
    }

        fun prepareBodyUpdateLaborActual(laborActualEntity: LaborActualEntity) : Labtran {
        val prepareBody = Labtran()
        prepareBody.startdatetime = laborActualEntity.startDateForMaximo
        prepareBody.finishdatetime = laborActualEntity.endDateForMaximo
        return prepareBody
    }

    fun handlingCreateLaborActual(member: Member, laborActualEntity: LaborActualEntity) {
        val woactivity = member.woactivity
        val labtrans = member.labtrans
        val isTask = laborActualEntity.isTask
        if(isTask == BaseParam.APP_TRUE) {
            woactivity.whatIfNotNullOrEmpty { listTask ->
                listTask.forEach {
                    it.labtrans.whatIfNotNullOrEmpty { labtrans ->
                        labtrans.forEach { labtran ->
                            laborActualEntity.wonumTask = it.wonum
                            checkingLaborActualExisting(labtran, laborActualEntity)
                        }
                    }
                }
            }
        } else {
            labtrans.whatIfNotNullOrEmpty { labtran ->
                labtran.forEach {
                    checkingLaborActualExisting(it, laborActualEntity)
                }
            }
        }
    }

    //handling labor plan cache when update labor plan to mx success
    fun handlingUpdateLaborActual(laborActualEntity: LaborActualEntity, syncUpdate: Int) {
        laborActualEntity.syncUpdate = syncUpdate
        saveLaborActualCache(laborActualEntity)
    }


    //Checking labor actual existing to replace labtransid
    private fun checkingLaborActualExisting(
        labtran: Labtran,
        laborActualEntity: LaborActualEntity
    ) {
        val labtransid = labtran.labtransid.toString()
        val cacheLaborPlan = findLaborActualByLabtransid(labtransid)
        if (cacheLaborPlan == null) {
            Timber.tag(TAG).i("checkingLaborActualExisting() update local cache after update")
            laborActualEntity.labtransid = labtransid
            laborActualEntity.syncUpdate = BaseParam.APP_TRUE
            laborActualEntity.isLocally = BaseParam.APP_FALSE
            saveLaborActualCache(laborActualEntity)
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

    fun fetchListCraftMaster(): List<CraftMasterEntity> {
        return craftMasterDao.getListCraft()
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

    /**
     * Method for locking labor plan and labor actual
     */

    fun lockingLaborActual(laborActualEntity: LaborActualEntity, locking: Boolean) {
        laborActualEntity.locking = locking
        saveLaborActualCache(laborActualEntity)
    }

    fun removeCacheLabor() {
        removeLabormaster()
        removeCraftMaster()
        removeLaborActual()
        removeLaborPlan()
    }
}