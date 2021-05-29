package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.MatusetransEntity
import id.thork.app.persistence.entity.MatusetransEntity_
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 28/05/2021
 * Jakarta, Indonesia.
 */
class MatusetransDaoImp : MatusetransDao {
    val TAG = MatusetransDaoImp::class.java.name

    var matusetransEntityBox: Box<MatusetransEntity>

    init {
        matusetransEntityBox = ObjectBox.boxStore.boxFor(MatusetransEntity::class.java)
    }

    private fun addUpdateInfo(matusetransEntity: MatusetransEntity, username: String) {
        matusetransEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                matusetransEntity.createdDate = Date()
                matusetransEntity.createdBy = username
            }
        )
        matusetransEntity.updatedDate = Date()
        matusetransEntity.updatedBy = username
    }

    override fun save(
        matusetransEntity: MatusetransEntity,
        username: String
    ) {
        addUpdateInfo(matusetransEntity, username)
        matusetransEntityBox.put(matusetransEntity)
    }

    override fun remove() {
        matusetransEntityBox.removeAll()
    }

    override fun findByWoid(workorderid: String): MatusetransEntity? {
        val materialEntity =
            matusetransEntityBox.query().equal(MatusetransEntity_.workorderId, workorderid).build()
                .find()
        materialEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findListMaterialActualByWoid(woid: String): List<MatusetransEntity>? {
        return matusetransEntityBox.query().equal(MatusetransEntity_.workorderId, woid).build()
            .find()
    }

    override fun findListMaterialActualByWonum(wonum: String): List<MatusetransEntity>? {
        return matusetransEntityBox.query().equal(MatusetransEntity_.wonum, wonum).build()
            .find()
    }

    override fun saveListMaterialActual(materialList: List<MatusetransEntity>): List<MatusetransEntity> {
        matusetransEntityBox.put(materialList)
        return materialList
    }

    override fun removeByEntity(matusetransEntity: MatusetransEntity) {
        matusetransEntityBox.remove(matusetransEntity)
    }

    override fun findByWoidAndItemnum(workorderid: String, itemnum: String): MatusetransEntity? {
        val materialEntity =
            matusetransEntityBox.query().equal(MatusetransEntity_.workorderId, workorderid)
                .equal(MatusetransEntity_.itemNum, itemnum).build()
                .find()
        materialEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    }