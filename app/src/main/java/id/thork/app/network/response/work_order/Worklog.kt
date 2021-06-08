package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Worklog(
    @Json(name = "class")
    val classX: String? = null,
    @Json(name = "clientviewable")
    val clientviewable: Boolean? = null,
    @Json(name = "createby")
    val createby: String? = null,
    @Json(name = "createdate")
    var createdate: String? = null,
    @Json(name = "description")
    var description: String? = null,
    @Json(name = "description_longdescription")
    var descriptionLongdescription: String? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "logtype")
    var logtype: String? = null,
    @Json(name = "logtype_description")
    val logtypeDescription: String? = null,
    @Json(name = "modifyby")
    val modifyby: String? = null,
    @Json(name = "modifydate")
    val modifydate: String? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "recordkey")
    var recordkey: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "worklogid")
    val worklogid: Int? = null
)