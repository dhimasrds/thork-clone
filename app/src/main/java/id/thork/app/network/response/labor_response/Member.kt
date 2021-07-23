package id.thork.app.network.response.labor_response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Member(
    @Json(name = "availfactor")
    val availfactor: Double? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "laborcode")
    val laborcode: String? = null,
    @Json(name = "laborcraftrate")
    val laborcraftrate: List<Laborcraftrate>? = null,
    @Json(name = "laborcraftrate_collectionref")
    val laborcraftrateCollectionref: String? = null,
    @Json(name = "laborid")
    val laborid: Int? = null,
    @Json(name = "lbsdatafromwo")
    val lbsdatafromwo: Boolean? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "personid")
    val personid: String? = null,
    @Json(name = "reportedhrs")
    val reportedhrs: Double? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "status")
    val status: String? = null,
    @Json(name = "status_description")
    val statusDescription: String? = null,
    @Json(name = "worksite")
    val worksite: String? = null,
    @Json(name = "ytdhrsrefused")
    val ytdhrsrefused: Double? = null,
    @Json(name = "ytdothrs")
    val ytdothrs: Double? = null
)