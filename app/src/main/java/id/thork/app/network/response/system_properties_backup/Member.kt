package id.thork.app.network.response.system_properties_backup


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
    @Json(name = "thisfsmresource")
    val thisfsmresource: List<Thisfsmresource>? = null,
    @Json(name = "thisfsmresource_collectionref")
    val thisfsmresourceCollectionref: String? = null,
    @Json(name = "thisfsmsysprop")
    val thisfsmsysprop: List<Thisfsmsysprop>? = null,
    @Json(name = "thisfsmsysprop_collectionref")
    val thisfsmsyspropCollectionref: String? = null
)