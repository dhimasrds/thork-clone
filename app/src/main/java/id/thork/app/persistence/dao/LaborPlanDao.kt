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
}