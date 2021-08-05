package id.thork.app.network.response.attendance_response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Member(
    @Json(name = "attendanceid")
    val attendanceid: Int? = null,
    @Json(name = "enterby")
    val enterby: String? = null,
    @Json(name = "enterdate")
    val enterdate: String? = null,
    @Json(name = "finishdate")
    var finishdate: String? = null,
    @Json(name = "finishtime")
    var finishtime: String? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "laborcode")
    var laborcode: String? = null,
    @Json(name = "laborhours")
    val laborhours: Double? = null,
    @Json(name = "orgid")
    var orgid: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "startdate")
    var startdate: String? = null,
    @Json(name = "starttime")
    var starttime: String? = null,
    @Json(name = "thisfsmlatitudey")
    var thisfsmlatitudey: Double? = null,
    @Json(name = "thisfsmlatitudeyout")
    var thisfsmlatitudeyout: Double? = null,
    @Json(name = "thisfsmlongitudex")
    var thisfsmlongitudex: Double? = null,
    @Json(name = "thisfsmlongitudexout")
    var thisfsmlongitudexout: Double? = null,
    @Json(name = "transdate")
    val transdate: String? = null
)