package id.thork.app.repository

import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.RetrofitBuilder
import id.thork.app.network.api.TaskApi
import id.thork.app.network.api.TaskClient
import id.thork.app.network.response.task_response.TaskResponse
import id.thork.app.network.response.task_response.Woactivity
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.TaskDao
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
) : BaseRepository {
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

    fun findTaskByWoIdAndScheduleDate(woid: Int, scheduleStart: String): List<TaskEntity> {
        return taskDao.findTaskByWoIdAndScheduleDate(woid, scheduleStart)
    }

    fun findTaskByWoIdAndSyncStatus(woid: Int, syncStatus: Int): List<TaskEntity> {
        return taskDao.findTaskByWoIdAndSyncStatus(woid, syncStatus)
    }

    fun findTaskByWoIdAndTaskId(woid: Int, taskId: Int): TaskEntity? {
        return taskDao.findTaskByWoidAndTaskId(woid, taskId)
    }

    fun saveCache(
        woid: Int?, wonum: String?, taskId: Int?,
        desc: String?, scheduleStart: String?, estDur: Double?, actualStart: String?,
        status: String?, syncStatus: Int?, offlineMode: Int?
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
            offlineMode = offlineMode
        )
        saveTaskCache(taskEntity)
    }

    fun removeTaskByWonum(wonum: String): Long {
        return taskDao.removeTaskByWonum(wonum)
    }

    fun removeTaskByWoidAndSyncStatus(woid: Int, syncStatus: Int): Long {
        return taskDao.removeTaskByWoidAndSyncStatus(woid, syncStatus)
    }

    suspend fun createTaskToMx(
        xMethodOverride: String?,
        contentType: String,
        cookie: String,
        patchType: String,
        woid: Int,
        prepareBody: TaskResponse,
        onSuccess: (Member) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = taskClient.createTask(
            xMethodOverride, contentType, cookie, patchType, woid,
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

    fun prepareTaskBody(woid: Int, scheduleStart: String): List<Woactivity> {
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

    fun handlingTaskSucces(list: List<id.thork.app.network.response.work_order.Woactivity>, woid: Int) {
        removeTaskByWoidAndSyncStatus(woid, BaseParam.APP_TRUE)
        for (tasks in list) {
            val taskEntity = TaskEntity(
                woId = tasks.workorderid,
                wonum = tasks.wonum,
                taskId = tasks.taskid,
                desc = tasks.description,
                scheduleStart = tasks.schedstart,
                estDuration = tasks.estdur,
                actualStart = tasks.actstart,
                status = tasks.status,
                syncStatus = BaseParam.APP_TRUE,
                offlineMode = BaseParam.APP_FALSE,
            )
            saveTaskCache(taskEntity)
        }
    }

    fun handlingTaskFailed(woid: Int, taskId: Int) {
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
            member.workorderid = 0
            member.taskid = it.taskId
            member.description = it.desc
            member.estdur = it.estDuration
            member.status = it.status
            member.schedstart = it.scheduleStart
            member.actstart = it.actualStart
            memberTask.add(member)
        }
        return memberTask
    }

    fun handlingTaskFailedFromCreateWo(woid: Int) {
        val taskEntity = findListTaskByWoid(woid)
        taskEntity.forEach {
            it.syncStatus = BaseParam.APP_FALSE
            it.offlineMode = BaseParam.APP_TRUE
            saveTaskCache(it)
        }
    }

    fun handlingTaskSuccessFromCreateWo(woid: Int) {
        val taskEntity = findListTaskByWoid(woid)
        taskEntity.forEach {
            it.syncStatus = BaseParam.APP_TRUE
            it.offlineMode = BaseParam.APP_FALSE
            saveTaskCache(it)
        }
    }

}