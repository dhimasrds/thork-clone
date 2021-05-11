package id.thork.app.network.response.asset_response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * Created by Raka Putra on 5/11/21
 * Jakarta, Indonesia.
 */

@JsonClass(generateAdapter = true)
data class Downtimereport(
    @Json(name = "currentstatus")
    var currentstatus: String? = null,

    @Json(name = "isrunning")
    var isrunning: Boolean? = null,

    @Json(name = "localref")
    var localref: String? = null,

    @Json(name = "currentstatus_description")
    var currentstatusDescription: Any? = null,

    @Json(name = "startdatesource")
    var startdatesource: String? = null,

    @Json(name = "isdowntimereport")
    var isdowntimereport: String? = null,

    @Json(name = "description")
    var description: String? = null,

    @Json(name = "operational")
    var operational: String? = null,

    @Json(name = "href")
    var href: String? = null,

    @Json(name = "statuschangedate")
    var statuschangedate: String? = null,

    @Json(name = "orgid")
    var orgid: String? = null,
)