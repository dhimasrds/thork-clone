package id.thork.app.network.response.google_maps


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Route(
    @Json(name = "bounds")
    val bounds: Bounds? = null,
    @Json(name = "copyrights")
    val copyrights: String? = null,
    @Json(name = "legs")
    val legs: List<Leg>? = null,
    @Json(name = "overview_polyline")
    val overviewPolyline: OverviewPolyline? = null,
    @Json(name = "summary")
    val summary: String? = null,
    @Json(name = "warnings")
    val warnings: List<Any>? = null,
    @Json(name = "waypoint_order")
    val waypointOrder: List<Any>? = null
)