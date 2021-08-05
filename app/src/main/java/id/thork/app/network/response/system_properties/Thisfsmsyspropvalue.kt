package id.thork.app.network.response.system_properties


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Thisfsmsyspropvalue(
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "thisfsmchangedate")
    val thisfsmchangedate: String? = null,
    @Json(name = "thisfsmisdefault")
    val thisfsmisdefault: Boolean? = null,
    @Json(name = "thisfsmmaxgroupid")
    val thisfsmmaxgroupid: String? = null,
    @Json(name = "thisfsmpropid")
    val thisfsmpropid: String? = null,
    @Json(name = "thisfsmpropvalue")
    val thisfsmpropvalue: String? = null,
    @Json(name = "thisfsmsyspropvalueid")
    val thisfsmsyspropvalueid: Int? = null
)