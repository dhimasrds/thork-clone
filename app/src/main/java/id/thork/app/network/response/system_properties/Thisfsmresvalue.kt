package id.thork.app.network.response.system_properties


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Thisfsmresvalue(
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "thisfsmblindcount")
    val thisfsmblindcount: Boolean? = null,
    @Json(name = "thisfsmisdefault")
    val thisfsmisdefault: Boolean? = null,
    @Json(name = "thisfsmmaxgroupid")
    val thisfsmmaxgroupid: String? = null,
    @Json(name = "thisfsmresvalueid")
    val thisfsmresvalueid: Int? = null
)