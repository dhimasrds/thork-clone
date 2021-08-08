package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.MatusetransEntity
import id.thork.app.persistence.entity.MatusetransEntity_
import id.thork.app.persistence.entity.WpmaterialEntity
import id.thork.app.persistence.entity.WpmaterialEntity_
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 28/05/2021
 * Jakarta, Indonesia.
 */
class WpmaterialDaoImp : WpmaterialDao {
    val TAG = WpmaterialDaoImp::class.java.name

    var wpmaterialEntityBox: Box<WpmaterialEntity>

    init {
        wpmaterialEntityBox = ObjectBox.boxStore.boxFor(WpmaterialEntity::class.java)
    }

    private fun addUpdateInfo(wpmaterialEntity: WpmaterialEntity, username: String) {
        wpmaterialEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                wpmaterialEntity.createdDate = Date()
                wpmaterialEntity.createdBy = username
            }
        )
        wpmaterialEntity.updatedDate = Date()
        wpmaterialEntity.updatedBy = username
    }

    override fun save(
        wpmaterialEntity: WpmaterialEntity,
        username: String
    ) {
        addUpdateInfo(wpmaterialEntity, username)
        wpmaterialEntityBox.put(wpmaterialEntity)
    }

    override fun delete(wpmaterialEntity: WpmaterialEntity) {
        wpmaterialEntityBox.remove(wpmaterialEntity)
    }

    override fun remove() {
        wpmaterialEntityBox.removeAll()
    }

    override fun findByWoid(workorderid: Int): WpmaterialEntity? {
        val materialEntity =
            wpmaterialEntityBox.query().equal(WpmaterialEntity_.workorderId, workorderid.toLong()).build()
                .find()
        materialEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findListMaterialActualByWoid(woid: Int): List<WpmaterialEntity> {
        return wpmaterialEntityBox.query().equal(WpmaterialEntity_.workorderId, woid.toLong()).build().find()
    }

    override fun saveListMaterialPlan(materialList: List<WpmaterialEntity>): List<WpmaterialEntity> {
        wpmaterialEntityBox.put(materialList)
        return materialList
    }

    override fun findByWoidAndItemnum(workorderid: Int, itemnum: String): WpmaterialEntity? {
        val materialEntity =
            wpmaterialEntityBox.query().equal(WpmaterialEntity_.workorderId, workorderid.toLong())
                .equal(WpmaterialEntity_.itemNum, itemnum).build()
                .find()
        materialEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findByIdAndWoId(id: Long, workorderid: Int): WpmaterialEntity? {
        val materialEntity =
            wpmaterialEntityBox.query()
                .equal(WpmaterialEntity_.id, id)
                .equal(WpmaterialEntity_.workorderId, workorderid.toLong())
                .build()
                .find()
        materialEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }
}