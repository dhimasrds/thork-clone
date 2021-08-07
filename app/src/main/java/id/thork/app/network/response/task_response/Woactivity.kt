package id.thork.app.network.response.task_response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Raka Putra on 7/5/21
 * Jakarta, Indonesia.
 */
@JsonClass(generateAdapter = true)
data class Woactivity (
    @Json(name = "workorderid")
    var workorderid: Int? = null,

    @Json(name = "taskid")
    var taskid: Int? = null,

    @Json(name = "wonum")
    var wonum: String? = null,

    @Json(name = "description")
    var description: String? = null,

    @Json(name = "estdur")
    var estdur: Double? = null,

    @Json(name = "status")
    var status: String? = null,

    @Json(name = "schedstart")
    var schedstart: String? = null,

    @Json(name = "actstart")
    var actstart: String? = null

)