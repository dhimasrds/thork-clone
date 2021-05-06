package id.thork.app.network.response.work_order

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Raka Putra on 5/5/21
 * Jakarta, Indonesia.
 */
@JsonClass(generateAdapter = true)
data class Labtran (
    @Json(name = "linecost")
    var linecost: Double? = null,

    @Json(name = "localref")
    var localref: String? = null,

    @Json(name = "enterdate")
    var enterdate: String? = null,

    @Json(name = "transtype")
    var transtype: String? = null,

    @Json(name = "craft")
    var craft: String? = null,

    @Json(name = "transtype_description")
    var transtypeDescription: String? = null,

    @Json(name = "transdate")
    var transdate: String? = null,

    @Json(name = "orgid")
    var orgid: String? = null,

    @Json(name = "laborcode")
    var laborcode: String? = null,

    @Json(name = "rollup")
    var rollup: Boolean? = null,

    @Json(name = "regularhrs")
    var regularhrs: Double? = null,

    @Json(name = "_rowstamp")
    var rowstamp: String? = null,

    @Json(name = "enterby")
    var enterby: String? = null,

    @Json(name = "labtransid")
    var labtransid: Int? = null,

    @Json(name = "startdateentered")
    var startdateentered: String? = null,

    @Json(name = "outside")
    var outside: Boolean? = null,

    @Json(name = "payrate")
    var payrate: Double? = null,

    @Json(name = "location")
    var location: String? = null,

    @Json(name = "href")
    var href: String? = null,

    @Json(name = "genapprservreceipt")
    var genapprservreceipt: Boolean? = null,

    @Json(name = "financialperiod")
    var financialperiod: String? = null,

    @Json(name = "refwo")
    var refwo: String? = null,

    @Json(name = "enteredastask")
    var enteredastask: Boolean? = null,
)