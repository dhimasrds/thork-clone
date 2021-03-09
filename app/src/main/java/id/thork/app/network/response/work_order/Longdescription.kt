package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Longdescription(
    @Json(name = "contentuid")
    val contentuid: String? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "langcode")
    val langcode: String? = null,
    @Json(name = "ldkey")
    val ldkey: Int? = null,
    @Json(name = "ldownercol")
    val ldownercol: String? = null,
    @Json(name = "ldownertable")
    val ldownertable: String? = null,
    @Json(name = "ldtext")
    var ldtext: String? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "longdescriptionid")
    val longdescriptionid: Int? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null
)