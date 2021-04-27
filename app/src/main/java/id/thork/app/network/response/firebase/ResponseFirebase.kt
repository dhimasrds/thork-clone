package id.thork.app.network.response.firebase


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseFirebase(
    @Json(name = "message_id")
    val messageId: Long? = null
)