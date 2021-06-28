package id.thork.app.repository

import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.persistence.dao.TaskDao
import id.thork.app.persistence.entity.TaskEntity
import javax.inject.Inject

/**
 * Created by Raka Putra on 6/23/21
 * Jakarta, Indonesia.
 */
class TaskRepository @Inject constructor(
    private val appSession: AppSession,
    private val taskDao: TaskDao,
) : BaseRepository {

    var laborCode = appSession.userEntity.laborcode
    var username = appSession.userEntity.username

    fun saveTaskCache(taskEntity: TaskEntity) {
        return taskDao.createTaskCache(taskEntity, username.toString())
    }

    fun removeTask() {
        return taskDao.removeTask()
    }

    fun findListTaskByWoid(woid: Int): List<TaskEntity> {
        return taskDao.findListTaskByWoid(woid)
    }

    fun saveCache(
        woid: Int?, wonum: String?, taskId: Int?,
        desc: String?, scheduleStart: String?, estDur: Double?, actualStart: String?,
        status: String?,
    ) {
        val taskEntity = TaskEntity(
            woId = woid,
            wonum = wonum,
            taskId = taskId,
            desc = desc,
            scheduleStart = scheduleStart,
            estDuration = estDur,
            actualStart = actualStart,
            status = status
        )
        saveTaskCache(taskEntity)
    }

    fun removeTaskByWonum(wonum: String): Long {
        return taskDao.removeTaskByWonum(wonum)
    }

}