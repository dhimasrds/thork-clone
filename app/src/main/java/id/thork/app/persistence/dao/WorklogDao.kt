package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.WorklogEntity

/**
 * Created by M.Reza Sulaiman on 07/06/2021
 * Jakarta, Indonesia.
 */
interface WorklogDao {
    fun save(worklogEntity: WorklogEntity, username: String)
    fun remove()
    fun findListWorklogByWoid(woid: String): List<WorklogEntity>
    fun saveListWorklog(worklogList: List<WorklogEntity>): List<WorklogEntity>
    fun findListWorklogByWoidSyncStatus(woid: String, syncStatus: Int): List<WorklogEntity>
    fun findWorklog(wonum: String, summary: String): WorklogEntity?
}