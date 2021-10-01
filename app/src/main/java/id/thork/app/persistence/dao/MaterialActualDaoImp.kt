package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseDao
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.*
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 28/05/2021
 * Jakarta, Indonesia.
 */
class MaterialActualDaoImp : MaterialActualDao, BaseDao() {
    val TAG = MaterialActualDaoImp::class.java.name

    var materialActualEntityBox: Box<MaterialActualEntity>

    init {
        materialActualEntityBox = ObjectBox.boxStore.boxFor(MaterialActualEntity::class.java)
    }

    override fun save(materialActualEntity: MaterialActualEntity, username: String) {
        addUpdateInfo(materialActualEntity, username)
        materialActualEntityBox.put(materialActualEntity)
        updateChangeDateWo(materialActualEntity.workorderId!!, username)
    }

    override fun save(
        materialList: List<MaterialActualEntity>,
        username: String
    ): List<MaterialActualEntity> {
        materialList.forEach {
            addUpdateInfo(it,username)
            updateChangeDateWo(it.workorderId!!.toInt(),username)
        }
        materialActualEntityBox.put(materialList)
        return materialList
    }

    override fun delete(materialActualEntity: MaterialActualEntity) {
        materialActualEntityBox.remove(materialActualEntity)
    }

    override fun remove() {
        materialActualEntityBox.removeAll()
    }

    override fun findByWoid(workorderid: Int): MaterialActualEntity? {
        val materialEntity =
            materialActualEntityBox.query().equal(MaterialActualEntity_.workorderId, workorderid.toLong()).build()
                .find()
        materialEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findListMaterialActualByWoid(woid: Int): List<MaterialActualEntity> {
        return materialActualEntityBox.query().equal(MaterialActualEntity_.workorderId, woid.toLong()).build().find()
    }

    override fun findByWoidAndItemnum(workorderid: Int, itemnum: String): MaterialActualEntity? {
        val materialEntity =
            materialActualEntityBox.query().equal(MaterialActualEntity_.workorderId, workorderid.toLong())
                .equal(MaterialActualEntity_.itemNum, itemnum).build()
                .find()
        materialEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findByIdAndWoId(id: Long, workorderid: Int): MaterialActualEntity? {
        val materialEntity =
            materialActualEntityBox.query()
                .equal(MaterialActualEntity_.id, id)
                .equal(MaterialActualEntity_.workorderId, workorderid.toLong())
                .build()
                .find()
        materialEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

}