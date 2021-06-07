package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.WorklogTypeEntity

/**
 * Created by M.Reza Sulaiman on 07/06/2021
 * Jakarta, Indonesia.
 */
interface WorklogTypeDao {
    fun save(worklogTypeEntity: WorklogTypeEntity, username: String)
    fun remove()
}