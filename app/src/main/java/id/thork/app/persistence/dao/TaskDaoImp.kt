package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.TaskEntity
import id.thork.app.persistence.entity.TaskEntity_
import io.objectbox.Box
import io.objectbox.kotlin.equal
import io.objectbox.query.QueryBuilder
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

        taskEntityBox.put(taskEntity
        )

    }

    override fun findListTaskByWoid(woid: Int): List<TaskEntity> {
        return taskEntityBox.query().equal(TaskEntity_.woId, woid)
            .order(TaskEntity_.taskId, QueryBuilder.DESCENDING or QueryBuilder.CASE_SENSITIVE)
            .build().find()
    }

    override fun findListTaskByTaskId(woid: Int): List<TaskEntity> {
        return taskEntityBox.query().equal(TaskEntity_.woId, woid)
            .order(TaskEntity_.taskId, QueryBuilder.DESCENDING or QueryBuilder.CASE_SENSITIVE)
            .build().find()
    }

}