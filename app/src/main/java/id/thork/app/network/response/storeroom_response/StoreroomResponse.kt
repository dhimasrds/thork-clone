package id.thork.app.network.response.storeroom_response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StoreroomResponse(
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "member")
    val member: List<Member>? = null,
    @Json(name = "responseInfo")
    val responseInfo: ResponseInfo? = null
)