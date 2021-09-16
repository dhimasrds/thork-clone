package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.LaborActualEntity

/**
 * Created by M.Reza Sulaiman on 30/07/2021
 * Jakarta, Indonesia.
 */
interface LaborActualDao {
    fun remove()
    fun createLaborActualCache(laborActualEntity: LaborActualEntity, username: String?)
    fun findlaborActualByworkorderid(laborcode: String, workorderid: String): LaborActualEntity?
    fun findListLaborActual(workorderid: String): List<LaborActualEntity>
    fun removeLaborActualByEntity(laborActualEntity: LaborActualEntity)
    fun findlaborActualByObjectBoxid(objectboxid: Long): LaborActualEntity?
}