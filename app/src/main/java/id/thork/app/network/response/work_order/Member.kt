package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Member(
    @Json(name = "apptrequired")
    var apptrequired: Boolean? = null,

    @Json(name = "assetnum")
    var assetnum: String? = null,

    @Json(name = "historyflag")
    var historyflag: Boolean? = null,

    @Json(name = "aos")
    var aos: Boolean? = null,

    @Json(name = "estservcost")
    var estservcost: Double? = null,

    @Json(name = "pluscismobile")
    var pluscismobile: Boolean? = null,

    @Json(name = "parentchgsstatus")
    var parentchgsstatus: Boolean? = null,

    @Json(name = "lms")
    var lms: Boolean? = null,

    @Json(name = "istask")
    var istask: Boolean? = null,

    @Json(name = "href")
    var href: String? = null,

    @Json(name = "estatapprmatcost")
    var estatapprmatcost: Double? = null,

    @Json(name = "worklog_collectionref")
    var worklogCollectionref: String? = null,

    @Json(name = "wopriority")
    var wopriority: Int? = null,

    @Json(name = "pluscloop")
    var pluscloop: Boolean? = null,

    @Json(name = "matusetrans_collectionref")
    var matusetransCollectionref: String? = null,

    @Json(name = "actmatcost")
    var actmatcost: Double? = null,

    @Json(name = "actlabhrs")
    var actlabhrs: Double? = null,

    @Json(name = "nestedjpinprocess")
    var nestedjpinprocess: Boolean? = null,

    @Json(name = "estatapprtoolcost")
    var estatapprtoolcost: Double? = null,

    @Json(name = "hasfollowupwork")
    var hasfollowupwork: Boolean? = null,

    @Json(name = "labtrans")
    var labtrans: List<Labtran>? = null,

    @Json(name = "actservcost")
    var actservcost: Double? = null,

    @Json(name = "flowactionassist")
    var flowactionassist: Boolean? = null,

    @Json(name = "ignorediavail")
    var ignorediavail: Boolean? = null,

    @Json(name = "locancestor_collectionref")
    var locancestorCollectionref: String? = null,

    @Json(name = "reqasstdwntime")
    var reqasstdwntime: Boolean? = null,

    @Json(name = "estmatcost")
    var estmatcost: Double? = null,

    @Json(name = "status")
    var status: String? = null,

    @Json(name = "labtrans_collectionref")
    var labtransCollectionref: String? = null,

    @Json(name = "estlabhrs")
    var estlabhrs: Double? = null,

    @Json(name = "esttoolcost")
    var esttoolcost: Double? = null,

    @Json(name = "reportedby")
    var reportedby: String? = null,

    @Json(name = "los")
    var los: Boolean? = null,

    @Json(name = "estoutlabcost")
    var estoutlabcost: Double? = null,

    @Json(name = "disabled")
    var disabled: Boolean? = null,

    @Json(name = "outmatcost")
    var outmatcost: Double? = null,

    @Json(name = "doclinks")
    var doclinks: Doclinks? = null,

    @Json(name = "estdur")
    var estdur: Double? = null,

    @Json(name = "changeby")
    var changeby: String? = null,

    @Json(name = "tooltrans_collectionref")
    var tooltransCollectionref: String? = null,

    @Json(name = "interruptible")
    var interruptible: Boolean? = null,

    @Json(name = "estlabcost")
    var estlabcost: Double? = null,

    @Json(name = "estatapprintlabhrs")
    var estatapprintlabhrs: Double? = null,

    @Json(name = "statusdate")
    var statusdate: String? = null,

    @Json(name = "wonum")
    var wonum: String? = null,

    @Json(name = "downtime")
    var downtime: Boolean? = null,

    @Json(name = "workorderid")
    var workorderid: Int? = null,

    @Json(name = "workorderspec_collectionref")
    var workorderspecCollectionref: String? = null,

    @Json(name = "acttoolcost")
    var acttoolcost: Double? = null,

    @Json(name = "actlabcost")
    var actlabcost: Double? = null,

    @Json(name = "actoutlabcost")
    var actoutlabcost: Double? = null,

    @Json(name = "wpmaterial_collectionref")
    var wpmaterialCollectionref: String? = null,

    @Json(name = "estatapprlabhrs")
    var estatapprlabhrs: Double? = null,

    @Json(name = "estatapprservcost")
    var estatapprservcost: Double? = null,

    @Json(name = "wostatus")
    var wostatus: List<Wostatu>? = null,

    @Json(name = "woactivity_collectionref")
    var woactivityCollectionref: String? = null,

    @Json(name = "estatapprlabcost")
    var estatapprlabcost: Double? = null,

    @Json(name = "lastcopylinkdate")
    var lastcopylinkdate: String? = null,

    @Json(name = "wplabor_collectionref")
    var wplaborCollectionref: String? = null,

    @Json(name = "ignoresrmavail")
    var ignoresrmavail: Boolean? = null,

    @Json(name = "outtoolcost")
    var outtoolcost: Double? = null,

    @Json(name = "estatapproutlabhrs")
    var estatapproutlabhrs: Double? = null,

    @Json(name = "_rowstamp")
    var rowstamp: String? = null,

    @Json(name = "estatapprintlabcost")
    var estatapprintlabcost: Double? = null,

    @Json(name = "firstapprstatus")
    var firstapprstatus: String? = null,

    @Json(name = "siteid")
    var siteid: String? = null,

    @Json(name = "multiassetlocci_collectionref")
    var multiassetlocciCollectionref: String? = null,

    @Json(name = "suspendflow")
    var suspendflow: Boolean? = null,

    @Json(name = "status_description")
    var statusDescription: String? = null,

    @Json(name = "woisswap")
    var woisswap: Boolean? = null,

    @Json(name = "actintlabhrs")
    var actintlabhrs: Double? = null,

    @Json(name = "woacceptscharges")
    var woacceptscharges: Boolean? = null,

    @Json(name = "repairlocflag")
    var repairlocflag: Boolean? = null,

    @Json(name = "changedate")
    var changedate: String? = null,

    @Json(name = "chargestore")
    var chargestore: Boolean? = null,

    @Json(name = "woclass_description")
    var woclassDescription: String? = null,

    @Json(name = "outlabcost")
    var outlabcost: Double? = null,

    @Json(name = "multiassetlocci")
    var multiassetlocci: List<Multiassetloccus>? = null,

    @Json(name = "orgid")
    var orgid: String? = null,

    @Json(name = "schedfinish")
    var schedfinish: String? = null,

    @Json(name = "woclass")
    var woclass: String? = null,

    @Json(name = "actoutlabhrs")
    var actoutlabhrs: Double? = null,

    @Json(name = "woserviceaddress")
    var woserviceaddress: List<Woserviceaddres>? = null,

    @Json(name = "locations_collectionref")
    var locationsCollectionref: String? = null,

    @Json(name = "wostatus_collectionref")
    var wostatusCollectionref: String? = null,

    @Json(name = "inctasksinsched")
    var inctasksinsched: Boolean? = null,

    @Json(name = "flowcontrolled")
    var flowcontrolled: Boolean? = null,

    @Json(name = "ams")
    var ams: Boolean? = null,

    @Json(name = "reportdate")
    var reportdate: String? = null,

    @Json(name = "description")
    var description: String? = null,

    @Json(name = "wpservice_collectionref")
    var wpserviceCollectionref: String? = null,

    @Json(name = "estatapproutlabcost")
    var estatapproutlabcost: Double? = null,

    @Json(name = "newchildclass")
    var newchildclass: String? = null,

    @Json(name = "djpapplied")
    var djpapplied: String? = null,

    @Json(name = "wptool_collectionref")
    var wptoolCollectionref: String? = null,

    @Json(name = "estoutlabhrs")
    var estoutlabhrs: Double? = null,

    @Json(name = "failurereport_collectionref")
    var failurereportCollectionref: String? = null,

    @Json(name = "assignment_collectionref")
    var assignmentCollectionref: String? = null,

    @Json(name = "actintlabcost")
    var actintlabcost: Double? = null,

    @Json(name = "assignment")
    var assignment: List<Assignment>? = null,

    @Json(name = "worktype")
    var worktype: String? = null,

    @Json(name = "estintlabhrs")
    var estintlabhrs: Double? = null,

    @Json(name = "milestone")
    var milestone: Boolean? = null,

    @Json(name = "wogroup")
    var wogroup: String? = null,

    @Json(name = "schedstart")
    var schedstart: String? = null,

    @Json(name = "location")
    var location: String? = null,

    @Json(name = "locations")
    var locations: Location? = null,

    @Json(name = "estintlabcost")
    var estintlabcost: Double? = null,

    @Json(name = "woserviceaddress_collectionref")
    var woserviceaddressCollectionref: String? = null,

    @Json(name = "haschildren")
    var haschildren: Boolean? = null,

    @Json(name = "longdescription_collectionref")
    var longdescriptionCollectionref: String? = null,

    @Json(name = "description_longdescription")
    var description_longdescription: String? = null,

    @Json(name = "longdescription")
    var longdescription: List<Longdescription>? = null,
)