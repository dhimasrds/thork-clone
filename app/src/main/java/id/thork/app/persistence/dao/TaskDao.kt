package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.TaskEntity

/**
 * Created by Raka Putra on 6/23/21
 * Jakarta, Indonesia.
 */
interface TaskDao {
    fun removeTask()
    fun createTaskCache(
        attendanceEntity: TaskEntity,
        username: String?,
    )

    fun findListTaskByWoid(woid: Int): List<TaskEntity>

    fun findListTaskByWonum(wonum: String): List<TaskEntity>

    fun removeTaskByWonum(wonum: String): Long

    fun findTaskByWoIdAndScheduleDate(woid: Int, scheduleDate: String): List<TaskEntity>

    fun findTaskByWoIdAndSyncStatus(woid: Int, syncStatus: Int): List<TaskEntity>

    fun findTaskByWoidAndTaskId(woid: Int, taskid: Int): TaskEntity?
}