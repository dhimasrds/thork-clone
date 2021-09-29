package id.thork.app.repository

import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.RetrofitBuilder
import id.thork.app.network.api.TaskApi
import id.thork.app.network.api.TaskClient
import id.thork.app.network.response.task_response.TaskResponse
import id.thork.app.network.response.task_response.Woactivity
import id.thork.app.network.response.work_order.Labtran
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.Wplabor
import id.thork.app.persistence.dao.LaborActualDao
import id.thork.app.persistence.dao.LaborPlanDao
import id.thork.app.persistence.dao.TaskDao
import id.thork.app.persistence.entity.LaborPlanEntity
import id.thork.app.persistence.entity.TaskEntity
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Raka Putra on 6/23/21
 * Jakarta, Indonesia.
 */
class TaskRepository @Inject constructor(
    private val appSession: AppSession,
    private val taskDao: TaskDao,
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
    private val preferenceManager: PreferenceManager,
    private val laborPlanDao: LaborPlanDao,
    private val laborActualDao: LaborActualDao,
) : BaseRepository() {
    val TAG = TaskRepository::class.java.name

    private val taskClient: TaskClient

    init {
        taskClient = TaskClient(provideTaskApi())
    }

    private fun provideTaskApi(): TaskApi {
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(TaskApi::class.java)
    }

    var laborCode = appSession.userEntity.laborcode
    var username = appSession.userEntity.username

    fun saveTaskCache(taskEntity: TaskEntity) {
        return taskDao.createTaskCache(taskEntity, username.toString())
    }

    fun removeAllTask() {
        return taskDao.removeTask()
    }

    fun findListTaskByWoid(woid: Int): List<TaskEntity> {
        return taskDao.findListTaskByWoid(woid)
    }

    fun findListTaskByWonum(wonum: String): List<TaskEntity> {
        return taskDao.findListTaskByWonum(wonum)
    }

    private fun findTaskByWoIdAndScheduleDate(woid: Int, scheduleStart: String): List<TaskEntity> {
        return taskDao.findTaskByWoIdAndScheduleDate(woid, scheduleStart)
    }

    private fun findTaskByWoIdAndSyncStatus(woid: Int, syncStatus: Int): List<TaskEntity> {
        return taskDao.findTaskByWoIdAndSyncStatus(woid, syncStatus)
    }

    fun findTaskByWoIdAndTaskId(woid: Int, taskId: Int): TaskEntity? {
        return taskDao.findTaskByWoidAndTaskId(woid, taskId)
    }

    fun findTaskListByWoidAndOfflineModeAndIsFromWoDetail(
        woid: Int,
        offlineMode: Int,
        isFromWoDetail: Int
    ): List<TaskEntity> {
        return taskDao.findTaskListByWoidAndOfflineModeAndIsFromWoDetail(
            woid,
            offlineMode,
            isFromWoDetail
        )
    }

    fun findTaskByOfflineModeAndIsFromWoDetail(offlineMode: Int, isFromWoDetail: Int): TaskEntity? {
        return taskDao.findTaskByOfflineModeAndIsFromWoDetail(offlineMode, isFromWoDetail)
    }

    fun findTaskListByOfflineModeAndIsFromWoDetail(
        offlineMode: Int,
        isFromWoDetail: Int
    ): List<TaskEntity> {
        return taskDao.findTaskListByOfflineModeAndIsFromWoDetail(offlineMode, isFromWoDetail)
    }

    fun saveCache(
        woid: Int?, wonum: String?, taskId: Int?,
        desc: String?, scheduleStart: String?, estDur: Double?, actualStart: String?,
        status: String?, syncStatus: Int?, offlineMode: Int?, isFromWoDetail: Int?
    ) {
        val taskEntity = TaskEntity(
            woId = woid,
            wonum = wonum,
            taskId = taskId,
            desc = desc,
            scheduleStart = scheduleStart,
            estDuration = estDur,
            actualStart = actualStart,
            status = status,
            syncStatus = syncStatus,
            offlineMode = offlineMode,
            isFromWoDetail = isFromWoDetail
        )
        saveTaskCache(taskEntity)
    }

    fun removeTaskByWonum(wonum: String): Long {
        return taskDao.removeTaskByWonum(wonum)
    }

    private fun removeTaskByWoidAndSyncStatus(woid: Int, syncStatus: Int): Long {
        return taskDao.removeTaskByWoidAndSyncStatus(woid, syncStatus)
    }

    private fun removeTaskByWoidAndTaskId(woid: Int, taskId: Int): Long {
        return taskDao.removeTaskByWoidAndTaskId(woid, taskId)
    }

    private fun removeTaskByWonumAndOfflineModeAndTaskId(
        wonum: String,
        offlineMode: Int,
        taskId: Int
    ): Long {
        return taskDao.removeTaskByWonumAndOfflineModeAndTaskId(wonum, offlineMode, taskId)
    }

    private fun removeTaskByWonumAndOfflineMode(wonum: String, offlineMode: Int): Long {
        return taskDao.removeTaskByWonumAndOfflineMode(wonum, offlineMode)
    }

    private fun removeTaskByWoidTask(woidTask: Int): Long {
        return taskDao.removeTaskByWoidTask(woidTask)
    }

    suspend fun createTaskToMx(
        xMethodOverride: String?,
        contentType: String,
        cookie: String,
        patchType: String,
        properties: String,
        woid: Int,
        prepareBody: TaskResponse,
        onSuccess: (Member) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = taskClient.createTask(
            xMethodOverride, contentType, cookie, patchType, properties, woid,
            prepareBody
        )

        response.suspendOnSuccess {
            data.whatIfNotNull {
                onSuccess(it)
            }
            Timber.tag(TAG).i("createTaskToMx() code: %s ", statusCode.code)

        }
            .onError {
                Timber.tag(TAG).i(
                    "createTaskToMx() code: %s error: %s",
                    statusCode.code,
                    message()
                )
                onError(message())
            }
    }

    fun prepareTaskBodyFromWoDetail(woid: Int, scheduleStart: String): List<Woactivity> {
        val taskEntity = findTaskByWoIdAndScheduleDate(woid, scheduleStart)
        val memberTask = mutableListOf<Woactivity>()
        taskEntity.forEach {
            val member = Woactivity()
            member.workorderid = 0
            member.taskid = it.taskId
            member.description = it.desc
            member.estdur = it.estDuration
            member.status = it.status
            member.schedstart = it.scheduleStart
            member.actstart = it.actualStart
            memberTask.add(member)
        }
        Timber.tag(TAG).d("prepareTaskBody() results: %s", memberTask)
        return memberTask
    }

    fun handlingTaskSuccesFromWoDetail(
        list: List<id.thork.app.network.response.work_order.Woactivity>,
        woid: Int,
        wonum: String
    ) {
        removeTaskByWoidAndSyncStatus(woid, BaseParam.APP_TRUE)
        for (tasks in list) {
            val taskEntity = TaskEntity(
                woId = woid,
                wonum = wonum,
                taskId = tasks.taskid,
                refWonum = tasks.wonum,
                workorderIdTask = tasks.workorderid,
                desc = tasks.description,
                scheduleStart = tasks.schedstart,
                estDuration = tasks.estdur,
                actualStart = tasks.actstart,
                status = tasks.status,
                syncStatus = BaseParam.APP_TRUE,
                offlineMode = BaseParam.APP_FALSE,
                isFromWoDetail = BaseParam.APP_TRUE
            )
            saveTaskCache(taskEntity)
        }
    }

    fun handlingTaskFailedFromWoDetail(woid: Int, taskId: Int) {
        val taskEntity = findTaskByWoIdAndTaskId(woid, taskId)
        taskEntity.whatIfNotNull {
            it.syncStatus = BaseParam.APP_FALSE
            it.offlineMode = BaseParam.APP_TRUE
            saveTaskCache(it)
        }
    }

    fun prepareTaskBodyFromCreateWo(woid: Int): List<id.thork.app.network.response.work_order.Woactivity> {
        val taskEntity = findTaskByWoIdAndSyncStatus(woid, BaseParam.APP_FALSE)
        val memberTask = mutableListOf<id.thork.app.network.response.work_order.Woactivity>()
        taskEntity.forEach {
            val member = id.thork.app.network.response.work_order.Woactivity()
            val listLaborPlan = prepareBodyLaborPlan(woid.toString(), it.taskId.toString())
            member.workorderid = 0
            member.taskid = it.taskId
            member.description = it.desc
            member.estdur = it.estDuration
            member.status = it.status
            member.schedstart = it.scheduleStart
            member.actstart = it.actualStart
            listLaborPlan.whatIfNotNullOrEmpty {
                member.wplabor = it
            }
            memberTask.add(member)
        }
        return memberTask
    }

    fun prepareBodyForCreateLaborPlanWithTask(woid: Int): List<id.thork.app.network.response.work_order.Woactivity> {
        val taskEntity = findTaskByWoIdAndSyncStatus(woid, BaseParam.APP_TRUE)
        val memberTask = mutableListOf<id.thork.app.network.response.work_order.Woactivity>()
        taskEntity.forEach {
            val member = id.thork.app.network.response.work_order.Woactivity()
            val listLaborPlan = prepareBodyLaborPlan(woid.toString(), it.taskId.toString())
            member.taskid = it.taskId
            member.wonum = it.refWonum
            listLaborPlan.whatIfNotNullOrEmpty {
                member.wplabor = it
            }
            memberTask.add(member)
        }
        return memberTask
    }

    fun prepareBodyForCreateLaborPlanWithTaskOfflinemode(laborPlanEntity: LaborPlanEntity): List<id.thork.app.network.response.work_order.Woactivity> {
        val memberTask = mutableListOf<id.thork.app.network.response.work_order.Woactivity>()
        if (laborPlanEntity.isTask == BaseParam.APP_TRUE) {
            laborPlanEntity.workorderid.whatIfNotNull {
                val woid = it.toInt()
                laborPlanEntity.taskid.whatIfNotNull { taskid ->
                    val member = id.thork.app.network.response.work_order.Woactivity()
                    val taskEntity = findTaskByWoIdAndTaskId(woid, taskid.toInt())
                    val wplabor = prepareBodyLaborPlanOfflinemode(laborPlanEntity)
                    taskEntity.whatIfNotNull { entity ->
                        member.taskid = entity.taskId
                        member.wonum = entity.refWonum
                        wplabor.whatIfNotNullOrEmpty { wplabor ->
                            member.wplabor = wplabor
                        }
                        memberTask.add(member)
                    }
                }
            }
        }
        return memberTask
    }

    private fun prepareBodyLaborPlanOfflinemode(laborPlanEntity: LaborPlanEntity): List<Wplabor> {
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

    fun handlingTaskFailedFromCreateWo(woid: Int) {
        val taskEntity = findListTaskByWoid(woid)
        taskEntity.forEach {
            it.syncStatus = BaseParam.APP_FALSE
            it.offlineMode = BaseParam.APP_TRUE
            saveTaskCache(it)
        }
    }

    fun handlingTaskSuccessFromCreateWo(
        member: Member,
        list: List<id.thork.app.network.response.work_order.Woactivity>,
        tempwonum: String
    ) {
        removeTaskByWonum(tempwonum)
        val woid = member.workorderid
        val wonumber = member.wonum
        for (tasks in list) {
            val taskEntity = TaskEntity(
                woId = woid,
                wonum = wonumber,
                taskId = tasks.taskid,
                refWonum = tasks.wonum,
                workorderIdTask = tasks.workorderid,
                desc = tasks.description,
                scheduleStart = tasks.schedstart,
                estDuration = tasks.estdur,
                actualStart = tasks.actstart,
                status = tasks.status,
                syncStatus = BaseParam.APP_TRUE,
                offlineMode = BaseParam.APP_FALSE,
                isFromWoDetail = BaseParam.APP_FALSE
            )
            saveTaskCache(taskEntity)
            Timber.tag(TAG)
                .d("handlingTaskSuccessFromCreateWo() size wplabor %s", tasks.wplabor?.size)
            tasks.wplabor.whatIfNotNullOrEmpty {
                handlingLaborPlan(it, member, tasks, tempwonum)
            }
        }
    }

    fun prepareTaskBodyInOfflineMode(listTask: TaskEntity): List<Woactivity> {
        val memberTask = mutableListOf<Woactivity>()
        val member = Woactivity()
        member.workorderid = 0
        member.taskid = listTask.taskId
        member.description = listTask.desc
        member.estdur = listTask.estDuration
        member.status = listTask.status
        member.schedstart = listTask.scheduleStart
        member.actstart = listTask.actualStart
        memberTask.add(member)
        return memberTask
    }

    fun handlingTaskSuccesInOfflineMode(
        list: List<id.thork.app.network.response.work_order.Woactivity>,
        taskEntity: TaskEntity,
        woid: Int
    ) {
        for (listTask in list) {
            if (taskEntity.woId == woid && taskEntity.taskId == listTask.taskid) {
                taskEntity.refWonum = listTask.wonum
                taskEntity.workorderIdTask = listTask.workorderid
                taskEntity.offlineMode = BaseParam.APP_FALSE
                taskEntity.syncStatus = BaseParam.APP_TRUE
                saveTaskCache(taskEntity)
            }
        }
    }

    suspend fun editTaskToMx(
        xMethodOverride: String?,
        contentType: String,
        cookie: String,
        patchType: String,
        properties: String,
        woid: Int,
        prepareBody: TaskResponse,
        onSuccess: (Member) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = taskClient.editTask(
            xMethodOverride, contentType, cookie, patchType, properties, woid,
            prepareBody
        )

        response.suspendOnSuccess {
            data.whatIfNotNull {
                onSuccess(it)
            }
            Timber.tag(TAG).i("createTaskToMx() code: %s ", statusCode.code)

        }
            .onError {
                Timber.tag(TAG).i(
                    "createTaskToMx() code: %s error: %s",
                    statusCode.code,
                    message()
                )
                onError(message())
            }
    }

    fun editTaskModule(
        woid: Int, taskId: Int,
        desc: String?, scheduleStart: String?, estDur: Double?, actualStart: String?
    ) {
        val taskEntity = findTaskByWoIdAndTaskId(woid, taskId)
        taskEntity.whatIfNotNull {
            it.desc = desc
            it.scheduleStart = scheduleStart
            it.estDuration = estDur
            it.actualStart = actualStart
            saveTaskCache(it)
        }
    }

    fun prepareTaskBodyForUpdateTask(woid: Int, scheduleStart: String): List<Woactivity> {
        val taskEntity = findTaskByWoIdAndScheduleDate(woid, scheduleStart)
        val memberTask = mutableListOf<Woactivity>()
        taskEntity.forEach {
            val member = Woactivity()
            member.taskid = it.taskId
            member.wonum = it.refWonum
            member.description = it.desc
            member.estdur = it.estDuration
            member.schedstart = it.scheduleStart
            member.actstart = it.actualStart
            memberTask.add(member)
        }
        Timber.tag(TAG).d("prepareTaskBody() results: %s", memberTask)
        return memberTask
    }

    suspend fun deleteTaskInMaximo(
        contentType: String,
        cookie: String,
        woidTask: Int,
        onSuccess: (Void) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = taskClient.deleteTask(
            contentType, cookie, woidTask
        )

        response.suspendOnSuccess {
            data.whatIfNotNull {
                onSuccess(it)
            }
            Timber.tag(TAG).i("createTaskToMx() code: %s ", statusCode.code)
            removeTaskByWoidTask(woidTask)
        }
            .onError {
                Timber.tag(TAG).i(
                    "createTaskToMx() code: %s error: %s",
                    statusCode.code,
                    message()
                )
                onError(message())
            }
    }

    fun prepareBodyLaborPlan(workroderid: String, taskid: String): List<Wplabor> {
        val listLaborPlan = laborPlanDao.findListLaborPlanlbyTaskid(workroderid, taskid)
        val wpLaborList = mutableListOf<Wplabor>()
        listLaborPlan.whatIfNotNull { listCache ->
            listCache.forEach { laborplan ->
                if (laborplan.syncUpdate == BaseParam.APP_FALSE) {
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

    fun handlingLaborPlan(
        wpLaborList: List<Wplabor>,
        member: Member,
        task: id.thork.app.network.response.work_order.Woactivity,
        tempwonum: String
    ) {
        val wonumheader = member.wonum
        val woidHeader = member.workorderid
        val taskid = task.taskid.toString()
        Timber.tag(TAG).d("handlingLaborPlan() list size : %s", wpLaborList.size)
        wpLaborList.whatIfNotNullOrEmpty { wplaborlist ->
            wplaborlist.forEach { wpLabor ->
                val laborcode = wpLabor.laborcode
                Timber.tag(TAG).d("handlingLaborPlan() Laborcode %s", wpLabor.laborcode)
                laborcode.whatIfNotNull(
                    whatIf = {
                        val laborCache = laborPlanDao.findlaborPlanByworkorderidAndTask(
                            laborcode.toString(),
                            tempwonum, taskid
                        )
                        Timber.tag(TAG).d("handlingLaborPlan() laborcache %s", laborCache)
                        laborCache.whatIfNotNull { cache ->
                            val wplaborid = wpLabor.wplaborid.toString()
                            val taskwonum = task.wonum.toString()

                            updateLaborPlanCache(
                                cache,
                                wplaborid,
                                wonumheader.toString(),
                                woidHeader.toString(),
                                taskwonum
                            )
                        }
                    },
                    whatIfNot = {
                        val laborCache = laborPlanDao.findlaborPlanBycraftAndTask(
                            wpLabor.craft.toString(),
                            tempwonum,
                            taskid
                        )
                        Timber.d("handlingLaborPlan() laborcode not avail %s", laborCache)
                        laborCache.whatIfNotNull { cache ->
                            val wplaborid = wpLabor.wplaborid.toString()
                            val taskwonum = task.wonum.toString()
                            Timber.d("handlingLaborPlan() save to local")
                            updateLaborPlanCache(
                                cache,
                                wplaborid,
                                wonumheader.toString(),
                                woidHeader.toString(),
                                taskwonum
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
        woidHeader: String,
        taskwonum: String
    ) {
        laborPlanEntity.wplaborid = wplaborid
        laborPlanEntity.wonumHeader = wonumheader
        laborPlanEntity.workorderid = woidHeader
        laborPlanEntity.wonumTask = taskwonum
        laborPlanEntity.syncUpdate = BaseParam.APP_TRUE
        laborPlanDao.createLaborPlanCache(laborPlanEntity, appSession.userEntity.username)
    }

    fun prepareBodyForCreateLaborActualWithTask(woid: Int): List<id.thork.app.network.response.work_order.Woactivity> {
        val taskEntity = findTaskByWoIdAndSyncStatus(woid, BaseParam.APP_TRUE)
        val memberTask = mutableListOf<id.thork.app.network.response.work_order.Woactivity>()
        taskEntity.forEach { task ->
            val member = id.thork.app.network.response.work_order.Woactivity()
            val listLaborActual = prepareBodyLaborActual(woid.toString(), task.taskId.toString())
            listLaborActual.whatIfNotNullOrEmpty {
                member.taskid = task.taskId
                member.wonum = task.refWonum
                member.labtrans = it
            }
            memberTask.add(member)
        }
        return memberTask
    }

    private fun prepareBodyLaborActual(workroderid: String, taskid: String): List<Labtran> {
        val listLaborActual = laborActualDao.findListLaborActualbyTaskid(workroderid, taskid)
        val labtransList = mutableListOf<Labtran>()
        listLaborActual.whatIfNotNull { listCache ->
            listCache.forEach { laboractual ->
                if (laboractual.syncUpdate == BaseParam.APP_FALSE) {
                    val labtran = Labtran()
                    laboractual.laborcode.whatIfNotNull(
                        whatIf = {
                            labtran.labtransid = 0
                            labtran.laborcode = it
                            labtran.startdatetime = laboractual.startDateForMaximo
                        }
                    )
                    labtransList.add(labtran)
                }
            }
        }
        return labtransList
    }



}