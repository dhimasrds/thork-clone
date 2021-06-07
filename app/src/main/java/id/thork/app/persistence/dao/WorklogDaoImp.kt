package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.WorklogEntity
import id.thork.app.persistence.entity.WorklogEntity_
import id.thork.app.persistence.entity.WpmaterialEntity
import id.thork.app.persistence.entity.WpmaterialEntity_
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 07/06/2021
 * Jakarta, Indonesia.
 */
class WorklogDaoImp : WorklogDao {
    val TAG = WorklogDaoImp::class.java.name

    var worklogEntityBox: Box<WorklogEntity>

    init {
        worklogEntityBox = ObjectBox.boxStore.boxFor(WorklogEntity::class.java)
    }

    private fun addUpdateInfo(worklogEntity: WorklogEntity, username: String) {
        worklogEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                worklogEntity.createdDate = Date()
                worklogEntity.createdBy = username
            }
        )
        worklogEntity.updatedDate = Date()
        worklogEntity.updatedBy = username
    }

    override fun save(
        worklogEntity: WorklogEntity,
        username: String
    ) {
        addUpdateInfo(worklogEntity, username)
        worklogEntityBox.put(worklogEntity)
    }

    override fun remove() {
        worklogEntityBox.removeAll()
    }

    override fun saveListWorklog(worklogList: List<WorklogEntity>): List<WorklogEntity> {
        worklogEntityBox.put(worklogList)
        return worklogList
    }

    override fun findListWorklogByWoid(woid: String): List<WorklogEntity> {
        return worklogEntityBox.query().equal(WorklogEntity_.workorderid, woid).build().find()
    }

}