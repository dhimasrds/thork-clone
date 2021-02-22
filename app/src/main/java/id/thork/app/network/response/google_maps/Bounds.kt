package id.thork.app.network.response.google_maps


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Bounds(
    @Json(name = "northeast")
    val northeast: Northeast? = null,
    @Json(name = "southwest")
    val southwest: Southwest? = null
)