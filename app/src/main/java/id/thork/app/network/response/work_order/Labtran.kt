package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class  Labtran(
    @Json(name = "assetnum")
    val assetnum: String? = null,
    @Json(name = "craft")
    val craft: String? = null,
    @Json(name = "enterby")
    val enterby: String? = null,
    @Json(name = "enterdate")
    val enterdate: String? = null,
    @Json(name = "enteredastask")
    val enteredastask: Boolean? = null,
    @Json(name = "financialperiod")
    val financialperiod: String? = null,
    @Json(name = "genapprservreceipt")
    val genapprservreceipt: Boolean? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "laborcode")
    var laborcode: String? = null,
    @Json(name = "labtransid")
    var labtransid: Int? = null,
    @Json(name = "linecost")
    val linecost: Double? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "location")
    val location: String? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "outside")
    val outside: Boolean? = null,
    @Json(name = "payrate")
    val payrate: Double? = null,
    @Json(name = "refwo")
    val refwo: String? = null,
    @Json(name = "regularhrs")
    val regularhrs: Double? = null,
    @Json(name = "rollup")
    val rollup: Boolean? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "startdateentered")
    val startdateentered: String? = null,
    @Json(name = "transdate")
    val transdate: String? = null,
    @Json(name = "transtype")
    val transtype: String? = null,
    @Json(name = "transtype_description")
    val transtypeDescription: String? = null,
    @Json(name = "skilllevel")
    val skilllevel: String? = null,
    @Json(name = "startdatetime")
    var startdatetime: String? = null,
    @Json(name = "finishdatetime")
    var finishdatetime: String? = null,
    @Json(name = "vendor")
    var vendor: String? = null,
)