package id.thork.app.network.response.labor_response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseInfo(
    @Json(name = "href")
    val href: String? = null
)