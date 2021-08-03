package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Wplabor(
    @Json(name = "apptrequired")
    val apptrequired: Boolean? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "laborcode")
    val laborcode: String? = null,
    @Json(name = "laborhrs")
    val laborhrs: Double? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "quantity")
    val quantity: Int? = null,
    @Json(name = "rate")
    val rate: Double? = null,
    @Json(name = "ratehaschanged")
    val ratehaschanged: Boolean? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "wplaborid")
    val wplaborid: String? = null,
    @Json(name = "wplaboruid")
    val wplaboruid: Int? = null,
    @Json(name = "craft")
    var craft: String? = null,
    @Json(name = "skilllevel")
    var skilllevel: String? = null,
    @Json(name = "vendor")
    var vendor: String? = null,
)