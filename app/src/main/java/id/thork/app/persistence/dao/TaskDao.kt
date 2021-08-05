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

    fun removeTaskByWoidAndSyncStatus(woid: Int, syncStatus: Int): Long

    fun removeTaskByWoidAndTaskId(woid: Int, taskid: Int): Long

    fun removeTaskByWonumAndOfflineModeAndTaskId(wonum: String, offlineMode: Int, taskid: Int): Long

    fun removeTaskByWonumAndOfflineMode(wonum: String, offlineMode: Int): Long

    fun findTaskByWoIdAndScheduleDate(woid: Int, scheduleDate: String): List<TaskEntity>

    fun findTaskByWoIdAndSyncStatus(woid: Int, syncStatus: Int): List<TaskEntity>

    fun findTaskByWoidAndTaskId(woid: Int, taskid: Int): TaskEntity?

    fun findTaskListByWoidAndOfflineModeAndIsFromWoDetail(woid: Int, offlineMode: Int, isFromWoDetail: Int): List<TaskEntity>

    fun findTaskByOfflineModeAndIsFromWoDetail(offlineMode: Int, isFromWoDetail: Int): TaskEntity?
    fun findTaskListByOfflineModeAndIsFromWoDetail(
        offlineMode: Int,
        isFromWoDetail: Int
    ): List<TaskEntity>
}