package id.thork.app.network.response.firebase


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FirebaseAndroid(
    @Json(name = "priority")
    var priority: String? = null
)