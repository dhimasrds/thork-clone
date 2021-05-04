package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity

/**
 * Created by M.Reza Sulaiman on 28/04/21
 * Jakarta, Indonesia.
 */
@Entity
class SysPropEntity : BaseEntity() {
    var fsmappid: Int? = null
    var fsmapp: String? = null
    var fsmappdescription: String? = null
    var siteid: String? = null
    var orgid: String? = null
    var propertiesid: String? = null
    var propertieskey: String? = null
    var propertiesvalue: String? = null
    var fsmisglobal: Boolean? = null


}