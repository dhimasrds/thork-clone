package id.thork.app.network.model.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WmsExtroleAppDetail(
    @Json(name = "enable")
    val enable: Boolean,
    @Json(name = "href")
    val href: String,
    @Json(name = "localref")
    val localref: String,
    @Json(name = "readonly")
    val readonly: Boolean,
    @Json(name = "_rowstamp")
    val rowstamp: String,
    @Json(name = "wms_extrole_app_detailid")
    val wmsExtroleAppDetailid: Int,
    @Json(name = "wmsappid")
    val wmsappid: String,
    @Json(name = "wmsappid_description")
    val wmsappidDescription: String,
    @Json(name = "wmsroleid")
    val wmsroleid: String
)