package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.MaterialStoreroomEntity
import id.thork.app.persistence.entity.StoreroomEntity
import id.thork.app.persistence.entity.WorklogEntity

/**
 * Created by Reja on 11/08/2021
 * Jakarta, Indonesia.
 */
interface MaterialStoreroomDao {
    fun save(materialStoreroomEntity: MaterialStoreroomEntity, username: String)
    fun save(materialStoreroomEntities: List<MaterialStoreroomEntity>, username: String)
    fun delete(materialStoreroomEntity: MaterialStoreroomEntity)
    fun delete()
    fun findStorerooms(): List<MaterialStoreroomEntity>
    fun findStorerooms(itemNum: String): List<MaterialStoreroomEntity>
}