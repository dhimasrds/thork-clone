package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.CraftMasterEntity

/**
 * Created by M.Reza Sulaiman on 03/08/2021
 * Jakarta, Indonesia.
 */
interface CraftMasterDao {
    fun createCraftCache(craftMasterEntity: CraftMasterEntity, username: String?)
    fun remove()
    fun getListCraftByLaborcode(laborcode: String): List<CraftMasterEntity>
    fun getCraftByLaborcode(laborcode: String): CraftMasterEntity?
    fun getCraft(): List<CraftMasterEntity>
}