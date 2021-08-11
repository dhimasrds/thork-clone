package id.thork.app.network.response.storeroom_response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "attachonissue")
    val attachonissue: Boolean? = null,
    @Json(name = "capitalized")
    val capitalized: Boolean? = null,
    @Json(name = "conditionenabled")
    val conditionenabled: Boolean? = null,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "hardresissue")
    val hardresissue: Boolean? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "_imagelibref")
    val imagelibref: String? = null,
    @Json(name = "inspectionrequired")
    val inspectionrequired: Boolean? = null,
    @Json(name = "iscrew")
    val iscrew: Boolean? = null,
    @Json(name = "iskit")
    val iskit: Boolean? = null,
    @Json(name = "itemid")
    val itemid: Int? = null,
    @Json(name = "itemtype")
    val itemtype: String? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "lottype")
    val lottype: String? = null,
    @Json(name = "lottype_description")
    val lottypeDescription: String? = null,
    @Json(name = "outside")
    val outside: Boolean? = null,
    @Json(name = "pluscisinhousecal")
    val pluscisinhousecal: Boolean? = null,
    @Json(name = "pluscismte")
    val pluscismte: Boolean? = null,
    @Json(name = "pluscsolution")
    val pluscsolution: Boolean? = null,
    @Json(name = "prorate")
    val prorate: Boolean? = null,
    @Json(name = "rotating")
    val rotating: Boolean? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "sparepartautoadd")
    val sparepartautoadd: Boolean? = null,
    @Json(name = "status")
    val status: String? = null,
    @Json(name = "status_description")
    val statusDescription: String? = null,
    @Json(name = "statusdate")
    val statusdate: String? = null,
    @Json(name = "taxexempt")
    val taxexempt: Boolean? = null,
    @Json(name = "thisfsmrfid")
    val thisfsmrfid: String? = null
)