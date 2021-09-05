package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.MaterialActualEntity

/**
 * Created by Reja on 05/09/2021
 * Jakarta, Indonesia.
 */
interface MaterialActualDao {
    fun save(materialActualEntity: MaterialActualEntity, username: String)
    fun save(materialList: List<MaterialActualEntity>, username: String): List<MaterialActualEntity>
    fun delete(materialActualEntity: MaterialActualEntity)
    fun remove()
    fun findByWoid(workorderid: Int): MaterialActualEntity?
    fun findListMaterialActualByWoid(woid: Int): List<MaterialActualEntity>
    fun findByWoidAndItemnum(workorderid: Int, itemnum: String): MaterialActualEntity?
    fun findByIdAndWoId(id: Long, workorderid: Int): MaterialActualEntity?
}