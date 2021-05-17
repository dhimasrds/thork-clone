package id.thork.app.network.response.fsm_location


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Member(
    @Json(name = "autowogen")
    var autowogen: Boolean?,
    @Json(name = "changeby")
    var changeby: String?,
    @Json(name = "changedate")
    var changedate: String?,
    @Json(name = "description")
    var description: String?,
    @Json(name = "disabled")
    var disabled: Boolean?,
    @Json(name = "expectedlife")
    var expectedlife: Int?,
    @Json(name = "href")
    var href: String?,
    @Json(name = "_imagelibref")
    var imagelibref: String?,
    @Json(name = "isdefault")
    var isdefault: Boolean?,
    @Json(name = "isrepairfacility")
    var isrepairfacility: Boolean?,
    @Json(name = "location")
    var location: String?,
    @Json(name = "locationsid")
    var locationsid: Int?,
    @Json(name = "orgid")
    var orgid: String?,
    @Json(name = "pluscloop")
    var pluscloop: Boolean?,
    @Json(name = "pluscpmextdate")
    var pluscpmextdate: Boolean?,
    @Json(name = "replacecost")
    var replacecost: Double?,
    @Json(name = "_rowstamp")
    var rowstamp: String?,
    @Json(name = "saddresscode")
    var saddresscode: String?,
    @Json(name = "serviceaddress")
    var serviceaddress: List<Serviceaddres>?,
    @Json(name = "serviceaddress_collectionref")
    var serviceaddressCollectionref: String?,
    @Json(name = "siteid")
    var siteid: String?,
    @Json(name = "status")
    var status: String?,
    @Json(name = "status_description")
    var statusDescription: String?,
    @Json(name = "statusdate")
    var statusdate: String?,
    @Json(name = "thisfsmrfid")
    var thisfsmrfid: String?,
    @Json(name = "thisfsmtagprogress")
    var thisfsmtagprogress: Boolean?,
    @Json(name = "thisfsmtagtime")
    var thisfsmtagtime: String?,
    @Json(name = "type")
    var type: String?,
    @Json(name = "type_description")
    var typeDescription: String?,
    @Json(name = "useinpopr")
    var useinpopr: Boolean?
)