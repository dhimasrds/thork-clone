package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity

/**
 * Created by M.Reza Sulaiman on 30/06/2021
 * Jakarta, Indonesia.
 */
@Entity
class LaborMasterEntity : BaseEntity() {
    var laborid: String? = null
    var laborcode: String? = null
    var personid: String? = null
    var worksite: String? = null
    var orgid: String? = null
    var status: String? = null
}