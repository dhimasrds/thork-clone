package id.thork.app.network.response.asset_response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * Created by Raka Putra on 5/11/21
 * Jakarta, Indonesia.
 */

@JsonClass(generateAdapter = true)
data class Assetmovedflt(
    @Json(name = "async")
    var async: Boolean? = null,

    @Json(name = "dfltnewparentchkbox")
    var dfltnewparentchkbox: Boolean? = null,

    @Json(name = "dfltneworgid")
    var dfltneworgid: String? = null,

    @Json(name = "toemailaddr")
    var toemailaddr: String? = null,

    @Json(name = "localref")
    var localref: String? = null,

    @Json(name = "dfltnewsite")
    var dfltnewsite: String? = null,

    @Json(name = "href")
    var href: String? = null,

    @Json(name = "dfltnewbinnumchkbox")
    var dfltnewbinnumchkbox: Boolean? = null,

    @Json(name = "orgid")
    var orgid: String? = null,

    @Json(name = "dfltnewlocationchkbox")
    var dfltnewlocationchkbox: Boolean? = null,
)