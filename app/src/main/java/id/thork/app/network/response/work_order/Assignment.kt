package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Assignment(
    @Json(name = "appointment")
    val appointment: Boolean? = null,
    @Json(name = "apptrequired")
    val apptrequired: Boolean? = null,
    @Json(name = "assignmentid")
    val assignmentid: Int? = null,
    @Json(name = "changeby")
    val changeby: String? = null,
    @Json(name = "changedate")
    val changedate: String? = null,
    @Json(name = "craft")
    val craft: String? = null,
    @Json(name = "finishdate")
    val finishdate: String? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "laborcode")
    val laborcode: String? = null,
    @Json(name = "laborhrs")
    val laborhrs: Double? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "parentassignid")
    val parentassignid: Int? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "scheduledate")
    val scheduledate: String? = null,
    @Json(name = "status")
    val status: String? = null,
    @Json(name = "status_description")
    val statusDescription: String? = null,
    @Json(name = "worklog")
    val worklog: Boolean? = null,
    @Json(name = "wplaborid")
    val wplaborid: String? = null
)