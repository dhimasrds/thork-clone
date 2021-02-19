package id.thork.app.network.response.google_maps


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OverviewPolyline(
    @Json(name = "points")
    val points: String? = null
)