package id.thork.app.network.model.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WmsExtrole(
    @Json(name = "href")
    val href: String,
    @Json(name = "localref")
    val localref: String,
    @Json(name = "orgid")
    val orgid: String,
    @Json(name = "_rowstamp")
    val rowstamp: String,
    @Json(name = "siteid")
    val siteid: String,
    @Json(name = "wms_extrole_app_detail")
    val wmsExtroleAppDetail: List<WmsExtroleAppDetail>,
    @Json(name = "wms_extrole_app_detail_collectionref")
    val wmsExtroleAppDetailCollectionref: String,
    @Json(name = "wms_extroleid")
    val wmsExtroleid: Int,
    @Json(name = "wmsroleid")
    val wmsroleid: String,
    @Json(name = "wmsrolename")
    val wmsrolename: String
)