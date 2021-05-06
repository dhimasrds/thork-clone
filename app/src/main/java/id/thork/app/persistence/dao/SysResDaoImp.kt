package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.SysResEntity
import id.thork.app.persistence.entity.SysResEntity_
import io.objectbox.Box
import java.util.*

/**
 * Created by M.Reza Sulaiman on 06/05/2021
 * Jakarta, Indonesia.
 */
class SysResDaoImp : SysResDao {
    var sysResEntityBox: Box<SysResEntity>

    init {
        sysResEntityBox = ObjectBox.boxStore.boxFor(SysResEntity::class.java)
    }

    private fun addUpdateInfo(sysResEntity: SysResEntity, username: String) {
        sysResEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                sysResEntity.createdDate = Date()
                sysResEntity.createdBy = username
            }
        )
        sysResEntity.updatedDate = Date()
        sysResEntity.updatedBy = username
    }

    override fun save(
        sysResEntity: SysResEntity,
        username: String
    ) {
        addUpdateInfo(sysResEntity, username)
        sysResEntityBox.put(sysResEntity)
    }

    override fun remove() {
        sysResEntityBox.removeAll()
    }

    override fun saveListSystemResource(sysResEntitylist: List<SysResEntity>): List<SysResEntity> {
        sysResEntityBox.put(sysResEntitylist)
        return sysResEntitylist
    }

    override fun findBypropertiesKey(resourcekey: String): SysResEntity? {
        val sysResEntity: List<SysResEntity> =
            sysResEntityBox.query().equal(SysResEntity_.resourcekey, resourcekey)
                .build().find()
        sysResEntity.whatIfNotNullOrEmpty {
            return sysResEntity[0]
        }
        return null
    }

}