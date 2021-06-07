package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity

/**
 * Created by M.Reza Sulaiman on 07/06/2021
 * Jakarta, Indonesia.
 */
@Entity
class WorklogEntity : BaseEntity() {
    var summary : String? = null
    var description: String? = null
    var type: String? = null
    var date: String? = null
    var wonum: String? = null
    var workorderid: String? = null
    var syncStatus: Int? = null
}