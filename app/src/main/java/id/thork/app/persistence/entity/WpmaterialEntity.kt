package id.thork.app.persistence.entity

import id.thork.app.helper.builder.LocoCheckBox
import id.thork.app.helper.builder.LocoLov
import id.thork.app.helper.builder.LocoLovExtension
import id.thork.app.helper.builder.LocoRadioButton
import io.objectbox.annotation.Entity

/**
 * Created by M.Reza Sulaiman on 28/05/2021
 * Jakarta, Indonesia.
 */
@Entity
class WpmaterialEntity : BaseEntity(){
    //Akan dihapus jika tidak dipakai
    var itemId : Int? = null
    @LocoLovExtension
    var itemNum : String? = null
    var description: String? = null
    @LocoRadioButton
    var lineType: String? = null
    var itemQty: Int? = null
    @LocoCheckBox
    var direqReq: Boolean = false
    var itemsetId: String? = null
    var wonum: String? = null
    var workorderId : Int? = null
    var siteid: String? = null
    var orgid: String? = null
    @LocoLov
    var storeroom: String? = null
}