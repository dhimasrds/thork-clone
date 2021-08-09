package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.WpmaterialEntity

/**
 * Created by M.Reza Sulaiman on 28/05/2021
 * Jakarta, Indonesia.
 */
interface WpmaterialDao {
    fun save(wpmaterialEntity: WpmaterialEntity, username: String)
    fun delete(wpmaterialEntity: WpmaterialEntity)
    fun remove()
    fun findByWoid(workorderid: Int): WpmaterialEntity?
    fun findListMaterialActualByWoid(woid: Int): List<WpmaterialEntity>
    fun saveListMaterialPlan(materialList: List<WpmaterialEntity>): List<WpmaterialEntity>
    fun findByWoidAndItemnum(workorderid: Int, itemnum: String): WpmaterialEntity?
    fun findByIdAndWoId(id: Long, workorderid: Int): WpmaterialEntity?
}