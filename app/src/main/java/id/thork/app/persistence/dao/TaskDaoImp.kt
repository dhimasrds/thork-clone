package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.TaskEntity
import id.thork.app.persistence.entity.TaskEntity_
import io.objectbox.Box
import io.objectbox.kotlin.equal
import timber.log.Timber
import java.util.*

/**
 * Created by Raka Putra on 6/23/21
 * Jakarta, Indonesia.
 */
class TaskDaoImp : TaskDao {
    val TAG = TaskDaoImp::class.java.name

    var taskEntityBox: Box<TaskEntity>

    init {
        taskEntityBox = ObjectBox.boxStore.boxFor(TaskEntity::class.java)
    }

    private fun addUpdateInfo(taskEntity: TaskEntity, username: String?) {
        taskEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                taskEntity.createdDate = Date()
                taskEntity.createdBy = username
            }
        )
        taskEntity.updatedDate = Date()
        taskEntity.updatedBy = username
    }

    override fun removeTask() {
        taskEntityBox.removeAll()
    }

    override fun createTaskCache(taskEntity: TaskEntity, username: String?) {
        addUpdateInfo(taskEntity, username)

        taskEntityBox.put(
            taskEntity
        )

    }

    override fun findListTaskByWoid(woid: Int): List<TaskEntity> {
        return taskEntityBox.query().equal(TaskEntity_.woId, woid)
            .order(TaskEntity_.taskId)
            .build().find()
    }

    override fun findListTaskByWonum(wonum: String): List<TaskEntity> {
        return taskEntityBox.query().equal(TaskEntity_.wonum, wonum)
            .order(TaskEntity_.taskId)
            .build().find()
    }

    override fun removeTaskByWonum(wonum: String): Long {
        return taskEntityBox.query().equal(TaskEntity_.wonum, wonum).build().remove()
    }

    override fun removeTaskByWoidAndSyncStatus(woid: Int, syncStatus: Int): Long {
        return taskEntityBox.query().equal(TaskEntity_.woId, woid)
            .equal(TaskEntity_.syncStatus, syncStatus).build().remove()
    }

    override fun removeTaskByWoidAndTaskId(woid: Int, taskId: Int): Long {
        return taskEntityBox.query().equal(TaskEntity_.woId, woid).equal(TaskEntity_.taskId, taskId)
            .build().remove()
    }

    override fun removeTaskByWonumAndOfflineModeAndTaskId(
        wonum: String,
        offlineMode: Int,
        taskId: Int
    ): Long {
        return taskEntityBox.query().equal(TaskEntity_.wonum, wonum)
            .equal(TaskEntity_.offlineMode, offlineMode).equal(TaskEntity_.taskId, taskId).build()
            .remove()
    }

    override fun removeTaskByWonumAndOfflineMode(wonum: String, offlineMode: Int): Long {
        return taskEntityBox.query().equal(TaskEntity_.wonum, wonum)
            .equal(TaskEntity_.offlineMode, offlineMode).build().remove()
    }

    override fun findTaskByWoIdAndScheduleDate(woid: Int, scheduleDate: String): List<TaskEntity> {
        return taskEntityBox.query().equal(TaskEntity_.woId, woid)
            .equal(TaskEntity_.scheduleStart, scheduleDate).build().find()
    }

    override fun findTaskByWoIdAndSyncStatus(woid: Int, syncStatus: Int): List<TaskEntity> {
        return taskEntityBox.query().equal(TaskEntity_.woId, woid)
            .equal(TaskEntity_.syncStatus, syncStatus).build().find()
    }

    override fun findTaskByWoidAndTaskId(woid: Int, taskId: Int): TaskEntity? {
        val taskEntity =
            taskEntityBox.query().equal(TaskEntity_.woId, woid).equal(TaskEntity_.taskId, taskId)
                .build()
                .find()
        taskEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findTaskListByWoidAndOfflineModeAndIsFromWoDetail(
        woid: Int,
        offlineMode: Int,
        isFromWoDetail: Int
    ): List<TaskEntity> {
        return taskEntityBox.query().equal(TaskEntity_.woId, woid)
            .equal(TaskEntity_.offlineMode, offlineMode)
            .equal(TaskEntity_.isFromWoDetail, isFromWoDetail).build().find()
    }

    override fun findTaskByOfflineModeAndIsFromWoDetail(
        offlineMode: Int,
        isFromWoDetail: Int
    ): TaskEntity? {
        val taskEntity =
            taskEntityBox.query().equal(TaskEntity_.offlineMode, offlineMode)
                .equal(TaskEntity_.isFromWoDetail, isFromWoDetail).build()
                .find()
        taskEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findTaskListByOfflineModeAndIsFromWoDetail(
        offlineMode: Int,
        isFromWoDetail: Int
    ): List<TaskEntity> {
        return taskEntityBox.query().equal(TaskEntity_.offlineMode, offlineMode)
            .equal(TaskEntity_.isFromWoDetail, isFromWoDetail).build()
            .find()
    }

}