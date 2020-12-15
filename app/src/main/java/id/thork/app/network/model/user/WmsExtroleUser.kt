package id.thork.app.network.model.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WmsExtroleUser(
    @Json(name = "href")
    val href: String,
    @Json(name = "localref")
    val localref: String,
    @Json(name = "_rowstamp")
    val rowstamp: String,
    @Json(name = "wms_extrole")
    val wmsExtrole: List<WmsExtrole>,
    @Json(name = "wms_extrole_collectionref")
    val wmsExtroleCollectionref: String,
    @Json(name = "wms_extrole_userid")
    val wmsExtroleUserid: Int,
    @Json(name = "wmsroleid")
    val wmsroleid: String,
    @Json(name = "wmsuserid")
    val wmsuserid: String,
    @Json(name = "wmsuserstatus")
    val wmsuserstatus: Boolean
)