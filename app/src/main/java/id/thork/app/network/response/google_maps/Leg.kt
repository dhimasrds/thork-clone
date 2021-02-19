package id.thork.app.network.response.google_maps


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Leg(
    @Json(name = "distance")
    val distance: Distance? = null,
    @Json(name = "duration")
    val duration: Duration? = null,
    @Json(name = "end_address")
    val endAddress: String? = null,
    @Json(name = "end_location")
    val endLocation: EndLocation? = null,
    @Json(name = "start_address")
    val startAddress: String? = null,
    @Json(name = "start_location")
    val startLocation: StartLocation? = null,
    @Json(name = "steps")
    val steps: List<Step>? = null,
    @Json(name = "traffic_speed_entry")
    val trafficSpeedEntry: List<Any>? = null,
    @Json(name = "via_waypoint")
    val viaWaypoint: List<Any>? = null
)