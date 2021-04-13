package id.thork.app.network.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Raka Putra on 3/29/21
 * Jakarta, Indonesia.
 */
@JsonClass(generateAdapter = true)
class ResponseApiKey {
    @Json(name = "apikey")
    var apikey: String? = null
}