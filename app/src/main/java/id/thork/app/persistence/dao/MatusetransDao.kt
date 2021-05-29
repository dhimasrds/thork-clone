package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.MatusetransEntity

/**
 * Created by M.Reza Sulaiman on 28/05/2021
 * Jakarta, Indonesia.
 */
interface MatusetransDao {
    fun save(matusetransEntity: MatusetransEntity, username: String)
    fun remove()
    fun findByWoid(workorderid: String): MatusetransEntity?
    fun findListMaterialActualByWoid(woid: String): List<MatusetransEntity>?
    fun findListMaterialActualByWonum(wonum: String): List<MatusetransEntity>?
    fun saveListMaterialActual(materialList: List<MatusetransEntity>): List<MatusetransEntity>
    fun removeByEntity(matusetransEntity: MatusetransEntity)
    fun findByWoidAndItemnum(workorderid: String, itemnum: String): MatusetransEntity?
}