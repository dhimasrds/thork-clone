package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.LaborPlanEntity

/**
 * Created by M.Reza Sulaiman on 29/07/2021
 * Jakarta, Indonesia.
 */
interface LaborPlanDao {
    fun remove()
    fun createLaborPlanCache(laborPlanEntity: LaborPlanEntity, username: String?)
    fun findlaborPlanByworkorderid(laborcode: String, workorderid: String): LaborPlanEntity?
    fun findListLaborPlan(workorderid: String): List<LaborPlanEntity>
    fun findlaborPlanByworkorderidandCraft(craft: String, workorderid: String): LaborPlanEntity?
    fun findlaborPlanBylaborcodedandCraft(laborcode: String, craft: String): LaborPlanEntity?
    fun findListLaborPlanlbyTaskid(workorderid: String, taskid: String): List<LaborPlanEntity>
    fun findlaborPlanByworkorderidAndTask(
        laborcode: String,
        wonum: String,
        taskid: String
    ): LaborPlanEntity?

    fun findListLaborPlanlbyWonumAndTaskid(wonum: String, taskid: String): List<LaborPlanEntity>
    fun findlaborPlanBycraftAndTask(
        craft: String,
        wonum: String,
        taskid: String
    ): LaborPlanEntity?

    fun findlaborPlanByWplaborid(wplaborid: String): LaborPlanEntity?
    fun removeByEntity(laborPlanEntity: LaborPlanEntity)
    fun findListLaborPlanlbySyncUpdateAndisDetailWo(
        syncupdate: Int,
        isLocally: Int
    ): List<LaborPlanEntity>

    fun findlaborPlanByObjectBoxid(objectboxid: Long): LaborPlanEntity?
}