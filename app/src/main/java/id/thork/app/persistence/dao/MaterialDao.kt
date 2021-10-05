package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.MaterialEntity

/**
 * Created by M.Reza Sulaiman on 28/05/2021
 * Jakarta, Indonesia.
 */
interface MaterialDao {
    fun save(materialEntity: MaterialEntity, username: String)
    fun remove()
    fun findByItemnum(itemnum: String): MaterialEntity?
    fun findBytagcode(tagcode: String): MaterialEntity?
    fun listMaterial(): List<MaterialEntity>?
    fun saveListMaterialMaster(materialList: List<MaterialEntity>, username: String): List<MaterialEntity>
}