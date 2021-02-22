package id.thork.app.network.response.google_maps


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StartLocation(
    @Json(name = "lat")
    val lat: Double? = null,
    @Json(name = "lng")
    val lng: Double? = null
)