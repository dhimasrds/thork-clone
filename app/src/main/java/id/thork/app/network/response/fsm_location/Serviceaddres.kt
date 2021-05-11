package id.thork.app.network.response.fsm_location


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Serviceaddres(
    @Json(name = "addresscode")
    var addresscode: String?,
    @Json(name = "description")
    var description: String?,
    @Json(name = "formattedaddress")
    var formattedaddress: String?,
    @Json(name = "href")
    var href: String?,
    @Json(name = "isweatherzone")
    var isweatherzone: Boolean?,
    @Json(name = "latitudey")
    var latitudey: Double?,
    @Json(name = "localref")
    var localref: String?,
    @Json(name = "longitudex")
    var longitudex: Double?,
    @Json(name = "orgid")
    var orgid: String?,
    @Json(name = "_rowstamp")
    var rowstamp: String?,
    @Json(name = "serviceaddressid")
    var serviceaddressid: Int?
)