package id.thork.app.network.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Raka Putra on 3/29/21
 * Jakarta, Indonesia.
 */
@JsonClass(generateAdapter = true)
class TokenApikey {
    @Json(name = "expiration")
    var expiration: Int? = null
}