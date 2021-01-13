package id.thork.app.network.response.work_order


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Location(
    @Json(name = "autowogen")
    val autowogen: Boolean,
    @Json(name = "changeby")
    val changeby: String,
    @Json(name = "changedate")
    val changedate: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "disabled")
    val disabled: Boolean,
    @Json(name = "expectedlife")
    val expectedlife: Int,
    @Json(name = "href")
    val href: String,
    @Json(name = "isdefault")
    val isdefault: Boolean,
    @Json(name = "isrepairfacility")
    val isrepairfacility: Boolean,
    @Json(name = "localref")
    val localref: String,
    @Json(name = "location")
    val location: String,
    @Json(name = "locationsid")
    val locationsid: Int,
    @Json(name = "orgid")
    val orgid: String,
    @Json(name = "pluscloop")
    val pluscloop: Boolean,
    @Json(name = "pluscpmextdate")
    val pluscpmextdate: Boolean,
    @Json(name = "replacecost")
    val replacecost: Double,
    @Json(name = "_rowstamp")
    val rowstamp: String,
    @Json(name = "saddresscode")
    val saddresscode: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "status_description")
    val statusDescription: String,
    @Json(name = "statusdate")
    val statusdate: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "type_description")
    val typeDescription: String,
    @Json(name = "useinpopr")
    val useinpopr: Boolean
)