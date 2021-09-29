package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity

/**
 * Created by M.Reza Sulaiman on 30/07/2021
 * Jakarta, Indonesia.
 */
@Entity
class LaborActualEntity: BaseEntity(){
    var laborcode: String? = null
    var labtransid: String? = null
    var taskid : String? = null
    var taskDescription: String? = null
    var craft: String? = null
    var skillLevel: String? = null
    var vendor: String? =  null
    var wonumHeader: String? = null
    var workorderid: String? = null
    var wonumTask: String? = null
    var startDate: String? = null
    var statTime: String? = null
    var endDate: String? = null
    var endTime: String? = null
    var startDateForMaximo: String? = null
    var endDateForMaximo: String? = null
    var syncUpdate: Int? = null
    var isLocally: Int? = null
    var isTask: Int? = null
    var locking: Boolean? = false
}