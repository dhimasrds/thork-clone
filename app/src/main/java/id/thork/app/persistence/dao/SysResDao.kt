package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.SysResEntity

/**
 * Created by M.Reza Sulaiman on 06/05/2021
 * Jakarta, Indonesia.
 */
interface SysResDao {
    fun save(sysResEntity: SysResEntity, username: String)
    fun remove()
    fun saveListSystemResource(sysResEntitylist: List<SysResEntity>): List<SysResEntity>
    fun findBypropertiesKey(resourcekey: String): SysResEntity?
}