package id.thork.app.network.response.firebase


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FirebaseData(
    @Json(name = "crewId")
    var crewId: String? = null,
    @Json(name = "laborcode")
    var laborcode: String? = null,
    @Json(name = "latitude")
    var latitude: String? = null,
    @Json(name = "longitude")
    var longitude: String? = null,
    @Json(name = "tag")
    var tag: String? = null
)