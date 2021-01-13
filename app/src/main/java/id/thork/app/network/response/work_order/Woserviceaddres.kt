package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Woserviceaddres(
    @Json(name = "addressischanged")
    val addressischanged: Boolean,
    @Json(name = "description")
    val description: String,
    @Json(name = "formattedaddress")
    val formattedaddress: String,
    @Json(name = "href")
    val href: String,
    @Json(name = "latitudey")
    val latitudey: Double,
    @Json(name = "localref")
    val localref: String,
    @Json(name = "longitudex")
    val longitudex: Double,
    @Json(name = "orgid")
    val orgid: String,
    @Json(name = "_rowstamp")
    val rowstamp: String,
    @Json(name = "saddresscode")
    val saddresscode: String,
    @Json(name = "woserviceaddressid")
    val woserviceaddressid: Int
)