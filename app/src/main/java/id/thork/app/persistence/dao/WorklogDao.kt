package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.WorklogEntity

/**
 * Created by M.Reza Sulaiman on 07/06/2021
 * Jakarta, Indonesia.
 */
interface WorklogDao {
    fun save(worklogEntity: WorklogEntity, username: String)
    fun remove()
    fun saveListMaterialPlan(worklogList: List<WorklogEntity>): List<WorklogEntity>
}