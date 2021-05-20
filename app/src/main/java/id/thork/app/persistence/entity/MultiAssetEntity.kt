package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity

/**
 * Created by M.Reza Sulaiman on 17/05/2021
 * Jakarta, Indonesia.
 */
@Entity
class MultiAssetEntity :  BaseEntity()  {
    var multiassetId : Int? = null
    var progress = false
    var asssetId : Int? = null
    var assetNum: String? = null
    var description: String? = null
    var image: String? = null
    var location: String? = null
    var thisfsmrfid: String? = null
    var thisfsmtagtime: String? = null
    var wonum: String? = null
    var workorderId : Int? = null
    var siteid: String? = null
    var orgid: String? = null
}