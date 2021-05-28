package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.WpmaterialEntity

/**
 * Created by M.Reza Sulaiman on 28/05/2021
 * Jakarta, Indonesia.
 */
interface WpmaterialDao {
    fun save(wpmaterialEntity: WpmaterialEntity, username: String)
    fun remove()
    fun findByWoid(workorderid: String): WpmaterialEntity?
    fun findListMaterialActualByWoid(woid: String): List<WpmaterialEntity>
    fun saveListMaterialPlan(materialList: List<WpmaterialEntity>): List<WpmaterialEntity>
}