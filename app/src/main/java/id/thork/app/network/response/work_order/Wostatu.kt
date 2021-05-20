package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Wostatu(
    @Json(name = "changeby")
    val changeby: String? = null,
    @Json(name = "changedate")
    val changedate: String? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "parent")
    val parent: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "status")
    val status: String? = null,
    @Json(name = "wostatusid")
    val wostatusid: Int? = null
)