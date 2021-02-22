package id.thork.app.network.response.google_maps


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DistanceX(
    @Json(name = "text")
    val text: String? = null,
    @Json(name = "value")
    val value: Int? = null
)