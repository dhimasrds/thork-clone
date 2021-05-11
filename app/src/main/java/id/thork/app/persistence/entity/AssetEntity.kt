package id.thork.app.persistence.entity

import android.os.Parcel
import android.os.Parcelable
import io.objectbox.annotation.Entity
import kotlinx.parcelize.Parcelize

/**
 * Created by Raka Putra on 5/11/21
 * Jakarta, Indonesia.
 */
@Parcelize
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
    var assetRfid: String? = null,
    var image: String? = null,
    var assetTagTime: String? = null
) :BaseEntity(), Parcelable{

}