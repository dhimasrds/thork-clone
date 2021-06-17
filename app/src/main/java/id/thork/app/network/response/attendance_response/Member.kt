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
    val finishdate: String? = null,
    @Json(name = "finishtime")
    val finishtime: String? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "laborcode")
    val laborcode: String? = null,
    @Json(name = "laborhours")
    val laborhours: Double? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "startdate")
    val startdate: String? = null,
    @Json(name = "starttime")
    val starttime: String? = null,
    @Json(name = "thisfsmlatitudey")
    val thisfsmlatitudey: Double? = null,
    @Json(name = "thisfsmlatitudeyout")
    val thisfsmlatitudeyout: Double? = null,
    @Json(name = "thisfsmlongitudex")
    val thisfsmlongitudex: Double? = null,
    @Json(name = "thisfsmlongitudexout")
    val thisfsmlongitudexout: Double? = null,
    @Json(name = "transdate")
    val transdate: String? = null
)