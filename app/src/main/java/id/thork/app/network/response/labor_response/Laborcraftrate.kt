package id.thork.app.network.response.labor_response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Laborcraftrate(
    @Json(name = "craft")
    val craft: String? = null,
    @Json(name = "defaultcraft")
    val defaultcraft: Boolean? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "inherit")
    val inherit: Boolean? = null,
    @Json(name = "laborcraftrateid")
    val laborcraftrateid: Int? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "rate")
    val rate: Double? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "skilllevel")
    val skilllevel: String? = null
)