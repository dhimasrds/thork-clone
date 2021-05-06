package id.thork.app.network.response.work_order

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Raka Putra on 5/5/21
 * Jakarta, Indonesia.
 */
@JsonClass(generateAdapter = true)
data class Assignment (
    @Json(name = "apptrequired")
    var apptrequired: Boolean? = null,

    @Json(name = "wplaborid")
    var wplaborid: String? = null,

    @Json(name = "status_description")
    var statusDescription: String? = null,

    @Json(name = "localref")
    var localref: String? = null,

    @Json(name = "changeby")
    var changeby: String? = null,

    @Json(name = "craft")
    var craft: String? = null,

    @Json(name = "finishdate")
    var finishdate: String? = null,

    @Json(name = "scheduledate")
    var scheduledate: String? = null,

    @Json(name = "appointment")
    var appointment: Boolean? = null,

    @Json(name = "changedate")
    var changedate: String? = null,

    @Json(name = "assignmentid")
    var assignmentid: Int? = null,

    @Json(name = "orgid")
    var orgid: String? = null,

    @Json(name = "laborcode")
    var laborcode: String? = null,

    @Json(name = "_rowstamp")
    var rowstamp: String? = null,

    @Json(name = "href")
    var href: String? = null,

    @Json(name = "worklog")
    var worklog: Boolean? = null,

    @Json(name = "parentassignid")
    var parentassignid: Int? = null,

    @Json(name = "laborhrs")
    var laborhrs: Double? = null,

    @Json(name = "status")
    var status: String? = null
)