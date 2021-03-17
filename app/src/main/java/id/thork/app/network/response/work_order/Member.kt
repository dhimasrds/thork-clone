package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Member(
    @Json(name = "actfinish")
    val actfinish: String? = null,
    @Json(name = "actintlabcost")
    val actintlabcost: Double? = null,
    @Json(name = "actintlabhrs")
    val actintlabhrs: Double? = null,
    @Json(name = "actlabcost")
    val actlabcost: Double? = null,
    @Json(name = "actlabhrs")
    val actlabhrs: Double? = null,
    @Json(name = "actmatcost")
    val actmatcost: Double? = null,
    @Json(name = "actoutlabcost")
    val actoutlabcost: Double? = null,
    @Json(name = "actoutlabhrs")
    val actoutlabhrs: Double? = null,
    @Json(name = "actservcost")
    val actservcost: Double? = null,
    @Json(name = "actstart")
    val actstart: String? = null,
    @Json(name = "acttoolcost")
    val acttoolcost: Double? = null,
    @Json(name = "ams")
    val ams: Boolean? = null,
    @Json(name = "aos")
    val aos: Boolean? = null,
    @Json(name = "apptrequired")
    val apptrequired: Boolean? = null,
    @Json(name = "assignment_collectionref")
    val assignmentCollectionref: String? = null,
    @Json(name = "calcpriority")
    val calcpriority: Int? = null,
    @Json(name = "changeby")
    val changeby: String? = null,
    @Json(name = "changedate")
    val changedate: String? = null,
    @Json(name = "chargestore")
    val chargestore: Boolean? = null,
    @Json(name = "cxlabor")
    val cxlabor: String? = null,
    @Json(name = "description")
    var description: String? = null,
    @Json(name = "disabled")
    val disabled: Boolean? = null,
    @Json(name = "doclinks")
    val doclinks: Doclinks? = null,
    @Json(name = "downtime")
    val downtime: Boolean? = null,
    @Json(name = "estatapprintlabcost")
    val estatapprintlabcost: Double? = null,
    @Json(name = "estatapprintlabhrs")
    val estatapprintlabhrs: Double? = null,
    @Json(name = "estatapprlabcost")
    val estatapprlabcost: Double? = null,
    @Json(name = "estatapprlabhrs")
    val estatapprlabhrs: Double? = null,
    @Json(name = "estatapprmatcost")
    val estatapprmatcost: Double? = null,
    @Json(name = "estatapproutlabcost")
    val estatapproutlabcost: Double? = null,
    @Json(name = "estatapproutlabhrs")
    val estatapproutlabhrs: Double? = null,
    @Json(name = "estatapprservcost")
    val estatapprservcost: Double? = null,
    @Json(name = "estatapprtoolcost")
    val estatapprtoolcost: Double? = null,
    @Json(name = "estdur")
    var estdur: Double? = null,
    @Json(name = "estintlabcost")
    val estintlabcost: Double? = null,
    @Json(name = "estintlabhrs")
    val estintlabhrs: Double? = null,
    @Json(name = "estlabcost")
    val estlabcost: Double? = null,
    @Json(name = "estlabhrs")
    val estlabhrs: Double? = null,
    @Json(name = "estmatcost")
    val estmatcost: Double? = null,
    @Json(name = "estoutlabcost")
    val estoutlabcost: Double? = null,
    @Json(name = "estoutlabhrs")
    val estoutlabhrs: Double? = null,
    @Json(name = "estservcost")
    val estservcost: Double? = null,
    @Json(name = "esttoolcost")
    val esttoolcost: Double? = null,
    @Json(name = "firstapprstatus")
    val firstapprstatus: String? = null,
    @Json(name = "flowactionassist")
    val flowactionassist: Boolean? = null,
    @Json(name = "flowcontrolled")
    val flowcontrolled: Boolean? = null,
    @Json(name = "haschildren")
    val haschildren: Boolean? = null,
    @Json(name = "hasfollowupwork")
    val hasfollowupwork: Boolean? = null,
    @Json(name = "historyflag")
    val historyflag: Boolean? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "ignorediavail")
    val ignorediavail: Boolean? = null,
    @Json(name = "ignoresrmavail")
    val ignoresrmavail: Boolean? = null,
    @Json(name = "inctasksinsched")
    val inctasksinsched: Boolean? = null,
    @Json(name = "interruptible")
    val interruptible: Boolean? = null,
    @Json(name = "istask")
    val istask: Boolean? = null,
    @Json(name = "lastcopylinkdate")
    val lastcopylinkdate: String? = null,
    @Json(name = "lead")
    val lead: String? = null,
    @Json(name = "lms")
    val lms: Boolean? = null,
    @Json(name = "location")
    var location: String? = null,
    @Json(name = "locations")
    val locations: List<Location>? = null,
    @Json(name = "locations_collectionref")
    val locationsCollectionref: String? = null,
    @Json(name = "longdescription")
    var longdescription: List<Longdescription>? = null,
    @Json(name = "longdescription_collectionref")
    val longdescriptionCollectionref: String? = null,
    @Json(name = "los")
    val los: Boolean? = null,
    @Json(name = "matusetrans_collectionref")
    val matusetransCollectionref: String? = null,
    @Json(name = "nestedjpinprocess")
    val nestedjpinprocess: Boolean? = null,
    @Json(name = "newchildclass")
    val newchildclass: String? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "outlabcost")
    val outlabcost: Double? = null,
    @Json(name = "outmatcost")
    val outmatcost: Double? = null,
    @Json(name = "outtoolcost")
    val outtoolcost: Double? = null,
    @Json(name = "parentchgsstatus")
    val parentchgsstatus: Boolean? = null,
    @Json(name = "pluscismobile")
    val pluscismobile: Boolean? = null,
    @Json(name = "pluscloop")
    val pluscloop: Boolean? = null,
    @Json(name = "repairlocflag")
    val repairlocflag: Boolean? = null,
    @Json(name = "reportdate")
    var reportdate: String? = null,
    @Json(name = "reportedby")
    val reportedby: String? = null,
    @Json(name = "reqasstdwntime")
    val reqasstdwntime: Boolean? = null,
    @Json(name = "respondby")
    val respondby: String? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "siteid")
    var siteid: String? = null,
    @Json(name = "status")
    var status: String? = null,
    @Json(name = "status_description")
    val statusDescription: String? = null,
    @Json(name = "statusdate")
    val statusdate: String? = null,
    @Json(name = "suspendflow")
    val suspendflow: Boolean? = null,
    @Json(name = "targcompdate")
    val targcompdate: String? = null,
    @Json(name = "targstartdate")
    val targstartdate: String? = null,
    @Json(name = "woacceptscharges")
    val woacceptscharges: Boolean? = null,
    @Json(name = "woclass")
    val woclass: String? = null,
    @Json(name = "woclass_description")
    val woclassDescription: String? = null,
    @Json(name = "wogroup")
    val wogroup: String? = null,
    @Json(name = "woisswap")
    val woisswap: Boolean? = null,
    @Json(name = "wonum")
    val wonum: String? = null,
    @Json(name = "wopriority")
    var wopriority: Int? = null,
    @Json(name = "workorderid")
    val workorderid: Int? = null,
    @Json(name = "woserviceaddress")
    var woserviceaddress: List<Woserviceaddres>? = null,
    @Json(name = "woserviceaddress_collectionref")
    val woserviceaddressCollectionref: String? = null,
    @Json(name = "wostatus")
    val wostatus: List<Wostatu>? = null,
    @Json(name = "wostatus_collectionref")
    val wostatusCollectionref: String? = null,
    @Json(name = "description_longdescription")
    var description_longdescription: String? = null
)