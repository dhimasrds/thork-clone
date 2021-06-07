package id.thork.app.network.response.worklogtype_response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Member(
    @Json(name = "defaults")
    val defaults: Boolean? = null,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "domainid")
    val domainid: String? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "maxvalue")
    val maxvalue: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "synonymdomainid")
    val synonymdomainid: Int? = null,
    @Json(name = "value")
    val value: String? = null,
    @Json(name = "valueid")
    val valueid: String? = null
)