package id.thork.app.persistence.dao

import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.MaterialBackupEntity
import id.thork.app.persistence.entity.MaterialBackupEntity_
import id.thork.app.persistence.entity.MaterialEntity_
import io.objectbox.Box
import io.objectbox.kotlin.equal

/**
 * Created by Raka Putra on 3/4/21
 * Jakarta, Indonesia.
 */
class MaterialDaoImp : MaterialDao {

    var materialBackupEntityBox: Box<MaterialBackupEntity>

    init {
        materialBackupEntityBox = ObjectBox.boxStore.boxFor(MaterialBackupEntity::class.java)
    }

    override fun saveMaterial(materialBackupEntity: MaterialBackupEntity): MaterialBackupEntity {
        materialBackupEntityBox.put(materialBackupEntity)
        return materialBackupEntity
    }

    override fun saveMaterialList(materialBackupEntity: List<MaterialBackupEntity?>?): List<MaterialBackupEntity?>? {
        materialBackupEntityBox.put(materialBackupEntity)
        return materialBackupEntity
    }

    override fun listMaterials(workorderid: Int): List<MaterialBackupEntity?>? {
        return materialBackupEntityBox.query().equal(MaterialBackupEntity_.workorderId, workorderid)
            .orderDesc(MaterialBackupEntity_.time).build().find()
    }

    override fun listMaterialsByWonum(wonum: String): List<MaterialBackupEntity?>? {
        return materialBackupEntityBox.query().equal(MaterialBackupEntity_.wonum, wonum)
            .orderDesc(MaterialBackupEntity_.time).build().find()
    }

    override fun listMaterialsByWoid(woid: Int): List<MaterialBackupEntity?>? {
        return materialBackupEntityBox.query().equal(MaterialBackupEntity_.workorderId, woid)
            .orderDesc(MaterialBackupEntity_.time).build().find()
    }

    override fun removeMaterialByWonum(wonum: String) : Long {
        return materialBackupEntityBox.query().equal(MaterialBackupEntity_.wonum, wonum).build().remove()
    }

    override fun removeMaterialByWoid(woid: Int) : Long {
        return materialBackupEntityBox.query().equal(MaterialBackupEntity_.workorderId, woid).build().remove()
    }
}