package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.SysPropEntity

/**
 * Created by M.Reza Sulaiman on 28/04/21
 * Jakarta, Indonesia.
 */
interface SysPropDao {
    fun remove()
    fun save(sysPropEntity: SysPropEntity, username: String)
    fun saveListSystemProperties(sysPropEntitylist: List<SysPropEntity>): List<SysPropEntity>
    fun findBypropertiesKey(propertiesKey: String): SysPropEntity?
}