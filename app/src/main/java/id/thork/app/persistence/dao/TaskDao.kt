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

    fun findListTaskByTaskId(woid: Int): List<TaskEntity>
}