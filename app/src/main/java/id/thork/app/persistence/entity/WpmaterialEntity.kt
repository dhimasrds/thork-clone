package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity

/**
 * Created by M.Reza Sulaiman on 28/05/2021
 * Jakarta, Indonesia.
 */
@Entity
class WpmaterialEntity : BaseEntity(){
    var itemId : Int? = null
    var itemNum : String? = null
    var description: String? = null
    var itemType: String? = null
    var itemsetId: String? = null
    var wonum: String? = null
    var workorderId : String? = null
    var siteid: String? = null
    var orgid: String? = null
}