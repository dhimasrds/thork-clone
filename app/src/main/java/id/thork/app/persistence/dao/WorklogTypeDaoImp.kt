package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseDao
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.WorklogTypeEntity
import id.thork.app.persistence.entity.WorklogTypeEntity_
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 07/06/2021
 * Jakarta, Indonesia.
 */
class WorklogTypeDaoImp : WorklogTypeDao,BaseDao() {
    val TAG = WorklogTypeDaoImp::class.java.name

    var worklogTypeEntityBox: Box<WorklogTypeEntity>

    init {
        worklogTypeEntityBox = ObjectBox.boxStore.boxFor(WorklogTypeEntity::class.java)
    }

    private fun addUpdateInfo(worklogTypeEntity: WorklogTypeEntity, username: String) {
        worklogTypeEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                worklogTypeEntity.createdDate = Date()
                worklogTypeEntity.createdBy = username
            }
        )
        worklogTypeEntity.updatedDate = Date()
        worklogTypeEntity.updatedBy = username
    }

    override fun save(
        worklogTypeEntity: WorklogTypeEntity,
        username: String
    ) {
        addUpdateInfo(worklogTypeEntity, username)
        worklogTypeEntityBox.put(worklogTypeEntity)
    }

    override fun remove() {
        worklogTypeEntityBox.removeAll()
    }

    override fun saveListWorklogType(worklogList: List<WorklogTypeEntity>): List<WorklogTypeEntity> {
        worklogTypeEntityBox.put(worklogList)
        return worklogList
    }

    override fun listWorklogType(): List<WorklogTypeEntity>? {
        return worklogTypeEntityBox.query().notNull(WorklogTypeEntity_.type).build()
            .find()
    }
}