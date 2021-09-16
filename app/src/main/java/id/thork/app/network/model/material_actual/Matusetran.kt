package id.thork.app.network.model.material_actual


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Matusetran(
    @Json(name = "issuetype")
    var issuetype: String? = null,
    @Json(name = "itemnum")
    var itemnum: String? = null,
    @Json(name = "linetype")
    var linetype: String? = null,
    @Json(name = "matusetransid")
    var matusetransid: Int? = null,
    @Json(name = "quantity")
    var quantity: Double? = null,
    @Json(name = "storeloc")
    var storeloc: String? = null,
    @Json(name = "unitcost")
    var unitcost: Double? = null
)