package id.thork.app.network.response.asset_response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * Created by Raka Putra on 5/11/21
 * Jakarta, Indonesia.
 */
@JsonClass(generateAdapter = true)
data class AssetResponse(
    @Json(name = "member")
    var member: List<Member>? = null,

    @Json(name = "href")
    var href: String? = null,

    @Json(name = "responseInfo")
    var responseInfo: ResponseInfo? = null,
)
