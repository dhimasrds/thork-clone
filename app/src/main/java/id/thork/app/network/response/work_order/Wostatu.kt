package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Wostatu(
    @Json(name = "changeby")
    val changeby: String,
    @Json(name = "changedate")
    val changedate: String,
    @Json(name = "href")
    val href: String,
    @Json(name = "localref")
    val localref: String,
//    @Json(name = "memo")
//    val memo: String,
    @Json(name = "orgid")
    val orgid: String,
    @Json(name = "parent")
    val parent: String,
    @Json(name = "_rowstamp")
    val rowstamp: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "wostatusid")
    val wostatusid: Int
)