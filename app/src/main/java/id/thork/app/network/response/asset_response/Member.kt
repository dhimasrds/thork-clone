package id.thork.app.network.response.asset_response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import id.thork.app.network.response.work_order.Doclinks


/**
 * Created by Raka Putra on 5/11/21
 * Jakarta, Indonesia.
 */

@JsonClass(generateAdapter = true)
data class Member(
    @Json(name = "ytdcost")
    var ytdcost: Double? = null,

    @Json(name = "moved")
    var moved: Boolean? = null,

    @Json(name = "pluscismte")
    var pluscismte: Boolean? = null,

    @Json(name = "pluscisinhousecal")
    var pluscisinhousecal: Boolean? = null,

    @Json(name = "totalcost")
    var totalcost: Double? = null,

    @Json(name = "assetstatus_collectionref")
    var assetstatusCollectionref: String? = null,

    @Json(name = "_rowstamp")
    var rowstamp: String? = null,

    @Json(name = "totunchargedcost")
    var totunchargedcost: Double? = null,

    @Json(name = "children")
    var children: Boolean? = null,

    @Json(name = "tloampartition")
    var tloampartition: Boolean? = null,

    @Json(name = "siteid")
    var siteid: String? = null,

    @Json(name = "unchargedcost")
    var unchargedcost: Double? = null,

    @Json(name = "href")
    var href: String? = null,

    @Json(name = "assetspec_collectionref")
    var assetspecCollectionref: String? = null,

    @Json(name = "mainthierchy")
    var mainthierchy: Boolean? = null,

    @Json(name = "assetuid")
    var assetuid: Int? = null,

    @Json(name = "status_description")
    var statusDescription: String? = null,

    @Json(name = "returnedtovendor")
    var returnedtovendor: Boolean? = null,

    @Json(name = "changedate")
    var changedate: String? = null,

    @Json(name = "replacecost")
    var replacecost: Double? = null,

    @Json(name = "orgid")
    var orgid: String? = null,

    @Json(name = "pluscsolution")
    var pluscsolution: Boolean? = null,

    @Json(name = "downtimereport_collectionref")
    var downtimereportCollectionref: String? = null,

    @Json(name = "saddresscode")
    var saddresscode: String? = null,

    @Json(name = "assetmovedflt")
    var assetmovedflt: List<Assetmovedflt>? = null,

    @Json(name = "serviceaddress_collectionref")
    var serviceaddressCollectionref: String? = null,

    @Json(name = "status")
    var status: String? = null,

    @Json(name = "assetmeter_collectionref")
    var assetmeterCollectionref: String? = null,

    @Json(name = "islinear")
    var islinear: Boolean? = null,

    @Json(name = "iscalibration")
    var iscalibration: Boolean? = null,

    @Json(name = "serviceaddress")
    var serviceaddress: List<Serviceaddress>? = null,

    @Json(name = "downtimereport")
    var downtimereport: List<Downtimereport>? = null,

    @Json(name = "description")
    var description: String? = null,

    @Json(name = "classspec_collectionref")
    var classspecCollectionref: String? = null,

    @Json(name = "totdowntime")
    var totdowntime: Double? = null,

    @Json(name = "_imagelibref")
    var imagelibref: String? = null,

    @Json(name = "pluscpmextdate")
    var pluscpmextdate: Boolean? = null,

    @Json(name = "assetnum")
    var assetnum: String? = null,

    @Json(name = "plusciscontam")
    var plusciscontam: Boolean? = null,

    @Json(name = "disabled")
    var disabled: Boolean? = null,

    @Json(name = "doclinks")
    var doclinks: Doclinks? = null,

    @Json(name = "changeby")
    var changeby: String? = null,

    @Json(name = "expectedlife")
    var expectedlife: Int? = null,

    @Json(name = "thisfsmrfid")
    var thisfsmrfid: String? = null,

    @Json(name = "invcost")
    var invcost: Double? = null,

    @Json(name = "purchaseprice")
    var purchaseprice: Double? = null,

    @Json(name = "statusdate")
    var statusdate: String? = null,

    @Json(name = "assetmovedflt_collectionref")
    var assetmovedfltCollectionref: String? = null,

    @Json(name = "isrunning")
    var isrunning: Boolean? = null,

    @Json(name = "budgetcost")
    var budgetcost: Double? = null,

    @Json(name = "assetid")
    var assetid: Int? = null,

    @Json(name = "autowogen")
    var autowogen: Boolean? = null,

    @Json(name = "itemsetid")
    var itemsetid: String? = null,

    @Json(name = "assettype_description")
    var assettypeDescription: String? = null,

    @Json(name = "thisfsmtagtime")
    var thisfsmtagtime: String? = null,

    @Json(name = "assettype")
    var assettype: String? = null,

    @Json(name = "location")
    var location: String? = null,
)