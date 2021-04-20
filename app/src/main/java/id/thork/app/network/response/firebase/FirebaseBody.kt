package id.thork.app.network.response.firebase


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FirebaseBody(
    @Json(name = "android")
    var firebaseAndroid: FirebaseAndroid? = null,
    @Json(name = "data")
    var firebaseData: FirebaseData? = null,
    @Json(name = "to")
    var to: String? = null
)