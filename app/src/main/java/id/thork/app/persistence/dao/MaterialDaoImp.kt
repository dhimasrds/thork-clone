package id.thork.app.persistence.dao

import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.MaterialEntity_
import io.objectbox.Box
import io.objectbox.kotlin.equal

/**
 * Created by Raka Putra on 3/4/21
 * Jakarta, Indonesia.
 */
class MaterialDaoImp : MaterialDao {

    var materialEntityBox: Box<MaterialEntity>

    init {
        materialEntityBox = ObjectBox.boxStore.boxFor(MaterialEntity::class.java)
    }

    override fun saveMaterial(materialEntity: MaterialEntity): MaterialEntity {
        materialEntityBox.put(materialEntity)
        return materialEntity
    }

    override fun saveMaterialList(materialEntity: List<MaterialEntity?>?): List<MaterialEntity?>? {
        materialEntityBox.put(materialEntity)
        return materialEntity
    }

    override fun listMaterials(workorderid: Int): List<MaterialEntity?>? {
        return materialEntityBox.query().equal(MaterialEntity_.workorderId, workorderid)
            .orderDesc(MaterialEntity_.time).build().find()
    }

    override fun listMaterialsByWonum(wonum: String): List<MaterialEntity?>? {
        return materialEntityBox.query().equal(MaterialEntity_.wonum, wonum)
            .orderDesc(MaterialEntity_.time).build().find()
    }

    override fun listMaterialsByWoid(woid: Int): List<MaterialEntity?>? {
        return materialEntityBox.query().equal(MaterialEntity_.workorderId, woid)
            .orderDesc(MaterialEntity_.time).build().find()
    }

    override fun removeMaterialByWonum(wonum: String) : Long {
        return materialEntityBox.query().equal(MaterialEntity_.wonum, wonum).build().remove()
    }

    override fun removeMaterialByWoid(woid: Int) : Long {
        return materialEntityBox.query().equal(MaterialEntity_.workorderId, woid).build().remove()
    }
}