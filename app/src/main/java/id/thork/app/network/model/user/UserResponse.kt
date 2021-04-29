package id.thork.app.network.model.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "href")
    val href: String,
    @Json(name = "member")
    val member: List<Member>,
    @Json(name = "responseInfo")
    val responseInfo: ResponseInfo,
) {
    constructor() : this(href = "", member = emptyList(), responseInfo = ResponseInfo(""))
}