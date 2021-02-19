package id.thork.app.network.response.google_maps


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseRoute(
    @Json(name = "geocoded_waypoints")
    val geocodedWaypoints: List<GeocodedWaypoint>? = null,
    @Json(name = "routes")
    val routes: List<Route>? = null,
    @Json(name = "status")
    val status: String? = null
)