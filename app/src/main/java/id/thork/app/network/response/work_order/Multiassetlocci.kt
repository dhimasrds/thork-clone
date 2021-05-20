package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Multiassetlocci(
    @Json(name = "asset")
    val asset: List<Asset>? = null,
    @Json(name = "asset_collectionref")
    val assetCollectionref: String? = null,
    @Json(name = "assetnum")
    val assetnum: String? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "isprimary")
    val isprimary: Boolean? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "location")
    val location: String? = null,
    @Json(name = "multiid")
    val multiid: Int? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "performmoveto")
    val performmoveto: Boolean? = null,
    @Json(name = "progress")
    val progress: Boolean? = null,
    @Json(name = "recordclass")
    val recordclass: String? = null,
    @Json(name = "recordkey")
    val recordkey: String? = null,
    @Json(name = "replacementsite")
    val replacementsite: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "totalworkunits")
    val totalworkunits: Double? = null,
    @Json(name = "workorgid")
    val workorgid: String? = null,
    @Json(name = "worksiteid")
    val worksiteid: String? = null
)