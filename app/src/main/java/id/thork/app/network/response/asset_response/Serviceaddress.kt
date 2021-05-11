package id.thork.app.network.response.asset_response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * Created by Raka Putra on 5/11/21
 * Jakarta, Indonesia.
 */

@JsonClass(generateAdapter = true)
data class Serviceaddress(
    @Json(name = "_rowstamp")
    var rowstamp: String? = null,

    @Json(name = "serviceaddressid")
    var serviceaddressid: Int? = null,

    @Json(name = "localref")
    var localref: String? = null,

    @Json(name = "latitudey")
    var latitudey: Double? = null,

    @Json(name = "longitudex")
    var longitudex: Double? = null,

    @Json(name = "description")
    var description: String? = null,

    @Json(name = "href")
    var href: String? = null,

    @Json(name = "formattedaddress")
    var formattedaddress: String? = null,

    @Json(name = "isweatherzone")
    var isweatherzone: Boolean? = null,

    @Json(name = "addresscode")
    var addresscode: String? = null,

    @Json(name = "orgid")
    var orgid: String? = null,
)