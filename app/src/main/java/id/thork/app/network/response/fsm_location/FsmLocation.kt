package id.thork.app.network.response.fsm_location


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FsmLocation(
    @Json(name = "href")
    var href: String?,
    @Json(name = "member")
    var member: List<Member>?,
    @Json(name = "responseInfo")
    var responseInfo: ResponseInfo?
)