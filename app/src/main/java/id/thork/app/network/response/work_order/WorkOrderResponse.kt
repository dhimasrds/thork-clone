package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WorkOrderResponse(
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "member")
    val member: List<Member>? = null,
    @Json(name = "responseInfo")
    val responseInfo: ResponseInfo? = null
)