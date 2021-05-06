package id.thork.app.persistence.entity

/**
 * Created by M.Reza Sulaiman on 06/05/2021
 * Jakarta, Indonesia.
 */
@io.objectbox.annotation.Entity
class SysResEntity : BaseEntity() {
    var fsmappid: Int? = null
    var fsmapp: String? = null
    var fsmappdescription: String? = null
    var siteid: String? = null
    var orgid: String? = null
    var resourceid: String? = null
    var resourcekey: String? = null
    var resourcevalue: String? = null
    var type: String? = null
}