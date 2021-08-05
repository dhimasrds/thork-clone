package id.thork.app.network.response.system_properties


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Member(
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "thisfsmapp")
    val thisfsmapp: String? = null,
    @Json(name = "thisfsmappid")
    val thisfsmappid: Int? = null,
    @Json(name = "thisfsmblindcount")
    val thisfsmblindcount: Boolean? = null,
    @Json(name = "thisfsmresvalue")
    val thisfsmresvalue: List<Thisfsmresvalue>? = null,
    @Json(name = "thisfsmresvalue_collectionref")
    val thisfsmresvalueCollectionref: String? = null,
    @Json(name = "thisfsmsyspropvalue")
    val thisfsmsyspropvalue: List<Thisfsmsyspropvalue>? = null,
    @Json(name = "thisfsmsyspropvalue_collectionref")
    val thisfsmsyspropvalueCollectionref: String? = null
)