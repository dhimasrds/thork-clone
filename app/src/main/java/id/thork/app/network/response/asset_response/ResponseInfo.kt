package id.thork.app.network.response.asset_response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * Created by Raka Putra on 5/11/21
 * Jakarta, Indonesia.
 */
@JsonClass(generateAdapter = true)
data class ResponseInfo(
    @Json(name = "href")
    var href: String? = null,
)