package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.MaterialEntity

/**
 * Created by Raka Putra on 3/4/21
 * Jakarta, Indonesia.
 */
interface MaterialDao {
    fun saveMaterial(materialEntity: MaterialEntity): MaterialEntity?

    fun listMaterials(workorderId: Int): List<MaterialEntity?>?
}