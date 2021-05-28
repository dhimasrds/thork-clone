package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Wpmaterial(
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "directreq")
    val directreq: Boolean? = null,
    @Json(name = "hours")
    val hours: Double? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "itemnum")
    val itemnum: String? = null,
    @Json(name = "itemqty")
    val itemqty: Double? = null,
    @Json(name = "itemsetid")
    val itemsetid: String? = null,
    @Json(name = "linecost")
    val linecost: Double? = null,
    @Json(name = "linetype")
    val linetype: String? = null,
    @Json(name = "linetype_description")
    val linetypeDescription: String? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "location")
    val location: String? = null,
    @Json(name = "mktplcitem")
    val mktplcitem: Boolean? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "rate")
    val rate: Double? = null,
    @Json(name = "ratehaschanged")
    val ratehaschanged: Boolean? = null,
    @Json(name = "requestby")
    val requestby: String? = null,
    @Json(name = "requestnum")
    val requestnum: String? = null,
    @Json(name = "requiredate")
    val requiredate: String? = null,
    @Json(name = "restype")
    val restype: String? = null,
    @Json(name = "restype_description")
    val restypeDescription: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "storelocsite")
    val storelocsite: String? = null,
    @Json(name = "unitcost")
    val unitcost: Double? = null,
    @Json(name = "unitcosthaschanged")
    val unitcosthaschanged: Boolean? = null,
    @Json(name = "wpitemid")
    val wpitemid: Int? = null
)