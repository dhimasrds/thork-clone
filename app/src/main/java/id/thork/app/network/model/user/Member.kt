package id.thork.app.network.model.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Member(
    @Json(name = "displayname")
    val displayname: String,
    @Json(name = "href")
    val href: String,
    @Json(name = "labor")
    val labor: List<Labor>,
    @Json(name = "labor_collectionref")
    val laborCollectionref: String,
    @Json(name = "locationorg")
    val locationorg: String,
    @Json(name = "locationsite")
    val locationsite: String,
    @Json(name = "maxuser")
    val maxuser: List<Maxuser>,
    @Json(name = "maxuser_collectionref")
    val maxuserCollectionref: String,
    @Json(name = "organization")
    val organization: List<Organization>,
    @Json(name = "organization_collectionref")
    val organizationCollectionref: String,
    @Json(name = "personid")
    val personid: String,
    @Json(name = "personuid")
    val personuid: Int,
    @Json(name = "_rowstamp")
    val rowstamp: String,
    @Json(name = "site")
    val site: List<Site>,
    @Json(name = "site_collectionref")
    val siteCollectionref: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "status_description")
    val statusDescription: String,
//    @Json(name = "wms_extrole_user")
//    val wmsExtroleUser: List<WmsExtroleUser>,
//    @Json(name = "wms_extrole_user_collectionref")
//    val wmsExtroleUserCollectionref: String
)