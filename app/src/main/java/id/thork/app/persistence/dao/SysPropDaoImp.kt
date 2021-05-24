package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.SysPropEntity
import id.thork.app.persistence.entity.SysPropEntity_
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 28/04/21
 * Jakarta, Indonesia.
 */
class SysPropDaoImp : SysPropDao {
    val TAG = SysPropDaoImp::class.java.name

    var sysPropEntityBox: Box<SysPropEntity>

    init {
        sysPropEntityBox = ObjectBox.boxStore.boxFor(SysPropEntity::class.java)
    }

    private fun addUpdateInfo(sysPropEntity: SysPropEntity, username: String) {
        sysPropEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                sysPropEntity.createdDate = Date()
                sysPropEntity.createdBy = username
            }
        )
        sysPropEntity.updatedDate = Date()
        sysPropEntity.updatedBy = username
    }

    override fun save(
        sysPropEntity: SysPropEntity,
        username: String
    ) {
        addUpdateInfo(sysPropEntity, username)
        sysPropEntityBox.put(sysPropEntity)
    }

    override fun remove() {
        sysPropEntityBox.removeAll()
    }

    override fun saveListSystemProperties(sysPropEntitylist: List<SysPropEntity>): List<SysPropEntity> {
        sysPropEntityBox.put(sysPropEntitylist)
        return sysPropEntitylist
    }

    override fun findBypropertiesKey(propertiesKey: String): SysPropEntity? {
        val sysPropEntity: List<SysPropEntity> =
            sysPropEntityBox.query().equal(SysPropEntity_.propertieskey, propertiesKey)
                .build().find()
        sysPropEntity.whatIfNotNullOrEmpty {
            return sysPropEntity[0]
        }
        return null
    }
}