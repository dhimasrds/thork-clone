package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.LaborMasterEntity
import id.thork.app.persistence.entity.LaborMasterEntity_
import id.thork.app.persistence.entity.LaborPlanEntity
import id.thork.app.persistence.entity.LaborPlanEntity_
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 03/08/2021
 * Jakarta, Indonesia.
 */
class LaborMasterDaoImp : LaborMasterDao {
    val TAG = LaborMasterDaoImp::class.java.name

    var laborMasterEntityBox: Box<LaborMasterEntity>

    init {
        laborMasterEntityBox = ObjectBox.boxStore.boxFor(LaborMasterEntity::class.java)
    }

    private fun addUpdateInfo(laborMasterEntity: LaborMasterEntity, username: String?) {
        laborMasterEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                laborMasterEntity.createdDate = Date()
                laborMasterEntity.createdBy = username
            }
        )
        laborMasterEntity.updatedDate = Date()
        laborMasterEntity.updatedBy = username
    }

    override fun remove() {
        laborMasterEntityBox.removeAll()
    }

    override fun createLaborMasterCache(laborMasterEntity: LaborMasterEntity, username: String?) {
        addUpdateInfo(laborMasterEntity, username)
        laborMasterEntityBox.put(laborMasterEntity)

    }

    override fun getListLaborMaster(): List<LaborMasterEntity> {
        return laborMasterEntityBox.query()
            .notNull(LaborMasterEntity_.laborcode)
            .build().find()
    }

}