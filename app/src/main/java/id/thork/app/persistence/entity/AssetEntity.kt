package id.thork.app.persistence.entity

import android.os.Parcelable
import io.objectbox.annotation.Entity
import kotlinx.parcelize.Parcelize

/**
 * Created by Raka Putra on 5/11/21
 * Jakarta, Indonesia.
 */
@Entity
data class AssetEntity(
    var assetnum: String? = null,
    var description: String? = null,
    var status: String? = null,
    var assetLocation: String? = null,
    var formattedaddress: String? = null,
    var siteid: String? = null,
    var orgid: String? = null,
    var latitudey: Double? = null,
    var longitudex: Double? = null,
    var assetrfid: String? = null,
    var image: String? = null,
    var assetTagTime: String? = null
) :BaseEntity(){

}