package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.StoreroomEntity
import id.thork.app.persistence.entity.WorklogEntity

/**
 * Created by Reja on 11/08/2021
 * Jakarta, Indonesia.
 */
interface StoreroomDao {
    fun save(storeroomEntity: StoreroomEntity, username: String)
    fun save(storeroomEntities: List<StoreroomEntity>, username: String)
    fun delete(storeroomEntity: StoreroomEntity)
    fun delete()
    fun findStorerooms(): List<StoreroomEntity>
    fun findStoreroom(location: String): List<StoreroomEntity>
}