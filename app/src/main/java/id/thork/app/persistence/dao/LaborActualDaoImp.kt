package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.LaborActualEntity
import id.thork.app.persistence.entity.LaborActualEntity_
import id.thork.app.persistence.entity.LaborPlanEntity
import id.thork.app.persistence.entity.LaborPlanEntity_
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 30/07/2021
 * Jakarta, Indonesia.
 */
class LaborActualDaoImp : LaborActualDao {
    val TAG = LaborActualDaoImp::class.java.name

    var laborActualEntityBox: Box<LaborActualEntity>

    init {
        laborActualEntityBox = ObjectBox.boxStore.boxFor(LaborActualEntity::class.java)
    }

    private fun addUpdateInfo(laborActualEntity: LaborActualEntity, username: String?) {
        laborActualEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                laborActualEntity.createdDate = Date()
                laborActualEntity.createdBy = username
            }
        )
        laborActualEntity.updatedDate = Date()
        laborActualEntity.updatedBy = username
    }

    override fun remove() {
        laborActualEntityBox.removeAll()
    }

    override fun createLaborActualCache(laborActualEntity: LaborActualEntity, username: String?) {
        addUpdateInfo(laborActualEntity, username)
        laborActualEntityBox.put(laborActualEntity)

    }

    override fun findlaborActualByworkorderid(
        laborcode: String,
        workorderid: String
    ): LaborActualEntity? {
        val laborPlanEntity =
            laborActualEntityBox.query().equal(LaborActualEntity_.laborcode, laborcode)
                .equal(LaborActualEntity_.workorderid, workorderid).build()
                .find()
        laborPlanEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findListLaborActual(workorderid: String): List<LaborActualEntity> {
        return laborActualEntityBox.query()
            .equal(LaborActualEntity_.workorderid, workorderid)
            .build().find()
    }
}