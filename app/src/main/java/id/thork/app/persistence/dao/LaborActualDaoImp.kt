package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseDao
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.LaborActualEntity
import id.thork.app.persistence.entity.LaborActualEntity_
import io.objectbox.Box
import io.objectbox.kotlin.equal
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 30/07/2021
 * Jakarta, Indonesia.
 */
class LaborActualDaoImp : LaborActualDao, BaseDao() {
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
        updateChangeDateWo(laborActualEntity.workorderid!!.toInt(), username)


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

    override fun removeLaborActualByEntity(laborActualEntity: LaborActualEntity) {
        laborActualEntityBox.remove(laborActualEntity)
    }


    override fun findlaborActualByObjectBoxid(
        objectboxid: Long
    ): LaborActualEntity? {
        val laborPlanEntity =
            laborActualEntityBox.query().equal(LaborActualEntity_.id, objectboxid)
                .build()
                .find()
        laborPlanEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findListLaborActualbyTaskid(
        workorderid: String,
        taskid: String
    ): List<LaborActualEntity> {
        return laborActualEntityBox.query().equal(LaborActualEntity_.workorderid, workorderid)
            .equal(LaborActualEntity_.taskid, taskid).build().find()
    }

    override fun findlaborActualByLabtransid(
        labtransid: String
    ): LaborActualEntity? {
        val laborActualEntity =
            laborActualEntityBox.query().equal(LaborActualEntity_.labtransid, labtransid)
                .build()
                .find()
        laborActualEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findListLaborActualSyncUpdateAndLocally(
        syncUpdate: Int,
        isLocally: Int
    ): List<LaborActualEntity> {
        return laborActualEntityBox.query().equal(LaborActualEntity_.syncUpdate, syncUpdate)
            .equal(LaborActualEntity_.isLocally, isLocally)
            .equal(LaborActualEntity_.locking, false).build().find()
    }

}