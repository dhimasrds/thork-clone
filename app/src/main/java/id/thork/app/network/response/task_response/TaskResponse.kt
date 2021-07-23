package id.thork.app.network.response.task_response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * Created by Raka Putra on 7/5/21
 * Jakarta, Indonesia.
 */
@JsonClass(generateAdapter = true)
data class TaskResponse(
    @Json(name = "woactivity")
    var woactivity: List<Woactivity>? = null
)