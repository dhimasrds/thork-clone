package id.thork.app.network.response.system_properties


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Thisfsmsysprop(
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "thisfsmisglobal")
    val thisfsmisglobal: Boolean? = null,
    @Json(name = "thisfsmpropid")
    val thisfsmpropid: String? = null,
    @Json(name = "thisfsmpropvalue")
    val thisfsmpropvalue: String? = null,
    @Json(name = "thisfsmsyspropid")
    val thisfsmsyspropid: Int? = null,
    @Json(name = "thisfsmsyspropvalue")
    val thisfsmsyspropvalue: List<Thisfsmsyspropvalue>? = null,
    @Json(name = "thisfsmsyspropvalue_collectionref")
    val thisfsmsyspropvalueCollectionref: String? = null
)