package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity

/**
 * Created by M.Reza Sulaiman on 29/07/2021
 * Jakarta, Indonesia.
 */
@Entity
class LaborPlanEntity : BaseEntity(){
    var laborcode: String? = null
    var wplaborid: String? = null
    var taskid : String? = null
    var taskDescription: String? = null
    var craft: String? = null
    var skillLevel: String? = null
    var vendor: String? =  null
    var wonumHeader: String? = null
    var workorderid: String? = null
    var wonumTask: String? = null
    var syncUpdate: Int? = null
    var isTask: Int? = null
    var isLocally: Int? = null

}