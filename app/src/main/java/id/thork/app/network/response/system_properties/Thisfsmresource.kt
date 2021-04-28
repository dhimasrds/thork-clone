package id.thork.app.network.response.system_properties


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Thisfsmresource(
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "thisfsmdescby")
    val thisfsmdescby: String? = null,
    @Json(name = "thisfsmresource")
    val thisfsmresource: String? = null,
    @Json(name = "thisfsmresourceid")
    val thisfsmresourceid: Int? = null,
    @Json(name = "thisfsmresvalue")
    val thisfsmresvalue: List<Thisfsmresvalue>? = null,
    @Json(name = "thisfsmresvalue_collectionref")
    val thisfsmresvalueCollectionref: String? = null,
    @Json(name = "thisfsmtype")
    val thisfsmtype: String? = null
)