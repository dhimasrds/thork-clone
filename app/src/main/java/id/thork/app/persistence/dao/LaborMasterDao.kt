package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.LaborMasterEntity

/**
 * Created by M.Reza Sulaiman on 03/08/2021
 * Jakarta, Indonesia.
 */
interface LaborMasterDao {
    fun createLaborMasterCache(laborMasterEntity: LaborMasterEntity, username: String?)
    fun remove()
    fun getListLaborMaster(): List<LaborMasterEntity>
}