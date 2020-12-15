package id.thork.app.network.model.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Labor(
    @Json(name = "availfactor")
    val availfactor: Double,
    @Json(name = "href")
    val href: String,
    @Json(name = "laborcode")
    val laborcode: String,
    @Json(name = "laborid")
    val laborid: Int,
    @Json(name = "lbsdatafromwo")
    val lbsdatafromwo: Boolean,
    @Json(name = "localref")
    val localref: String,
    @Json(name = "orgid")
    val orgid: String,
    @Json(name = "reportedhrs")
    val reportedhrs: Double,
    @Json(name = "_rowstamp")
    val rowstamp: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "status_description")
    val statusDescription: String,
    @Json(name = "ytdhrsrefused")
    val ytdhrsrefused: Double,
    @Json(name = "ytdothrs")
    val ytdothrs: Double
)