package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Woserviceaddres(
    @Json(name = "addressischanged")
    val addressischanged: Boolean? = null,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "formattedaddress")
    val formattedaddress: String? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "latitudey")
    val latitudey: Double? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "longitudex")
    val longitudex: Double? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "saddresscode")
    val saddresscode: String? = null,
    @Json(name = "woserviceaddressid")
    val woserviceaddressid: Int? = null
)