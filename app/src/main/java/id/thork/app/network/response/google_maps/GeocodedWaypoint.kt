package id.thork.app.network.response.google_maps


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodedWaypoint(
    @Json(name = "geocoder_status")
    val geocoderStatus: String? = null,
    @Json(name = "place_id")
    val placeId: String? = null,
    @Json(name = "types")
    val types: List<String>? = null
)