package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Longdescription(
    @Json(name = "contentuid")
    val contentuid: String,
    @Json(name = "href")
    val href: String,
    @Json(name = "langcode")
    val langcode: String,
    @Json(name = "ldkey")
    val ldkey: Int,
    @Json(name = "ldownercol")
    val ldownercol: String,
    @Json(name = "ldownertable")
    val ldownertable: String,
    @Json(name = "ldtext")
    val ldtext: String,
    @Json(name = "localref")
    val localref: String,
    @Json(name = "longdescriptionid")
    val longdescriptionid: Int,
    @Json(name = "_rowstamp")
    val rowstamp: String
)