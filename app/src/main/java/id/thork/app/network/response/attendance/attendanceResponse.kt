package id.thork.app.network.response.attendance


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class attendanceResponse(
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "member")
    val member: List<Member>? = null,
    @Json(name = "responseInfo")
    val responseInfo: ResponseInfo? = null
)