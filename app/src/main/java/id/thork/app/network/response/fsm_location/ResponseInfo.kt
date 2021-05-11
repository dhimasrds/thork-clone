package id.thork.app.network.response.fsm_location


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseInfo(
    @Json(name = "href")
    var href: String?
)