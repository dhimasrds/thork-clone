package id.thork.app.network.response.work_order

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Raka Putra on 5/5/21
 * Jakarta, Indonesia.
 */
@JsonClass(generateAdapter = true)
data class Multiassetloccus(
    @Json(name = "multiid")
    var multiid: Int? = null,

    @Json(name = "localref")
    var localref: String? = null,

    @Json(name = "isprimary")
    var isprimary: Boolean? = null,

    @Json(name = "orgid")
    var orgid: String? = null,

    @Json(name = "workorgid")
    var workorgid: String? = null,

    @Json(name = "_rowstamp")
    var rowstamp: String? = null,

    @Json(name = "replacementsite")
    var replacementsite: String? = null,

    @Json(name = "recordkey")
    var recordkey: String? = null,

    @Json(name = "assetnum")
    var assetnum: String? = null,

    @Json(name = "worksiteid")
    var worksiteid: String? = null,

    @Json(name = "progress")
    var progress: Boolean? = null,

    @Json(name = "location")
    var location: String? = null,

    @Json(name = "href")
    var href: String? = null,

    @Json(name = "recordclass")
    var recordclass: String? = null,

    @Json(name = "asset")
    var asset: List<Asset>? = null,

    @Json(name = "performmoveto")
    var performmoveto: Boolean? = null,

    @Json(name = "totalworkunits")
    var totalworkunits: Double? = null,

    @Json(name = "asset_collectionref")
    var assetCollectionref: String? = null,
)