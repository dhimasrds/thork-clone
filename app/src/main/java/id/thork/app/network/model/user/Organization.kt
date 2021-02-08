package id.thork.app.network.model.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class  Organization(
    @Json(name = "description")
    val description: String,
    @Json(name = "href")
    val href: String,
    @Json(name = "localref")
    val localref: String,
    @Json(name = "orgid")
    val orgid: String,
    @Json(name = "_rowstamp")
    val rowstamp: String
)