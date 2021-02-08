package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WorkOrderResponse(
    @Json(name = "href")
    val href: String,
    @Json(name = "member")
    val member: List<Member>,
    @Json(name = "responseInfo")
    val responseInfo: ResponseInfo
)
{
    constructor() : this(href = "", member = emptyList(), responseInfo = ResponseInfo(
        ""))
}