/*
 * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * This.ID. ("Confidential Information").
 *
 * Such Confidential Information shall not be disclosed and shall
 * use it only	 in accordance with the terms of the license agreement
 * entered into with This.ID; other than in accordance with the written
 * permission of This.ID.
 */

package id.thork.app.network.response.material_response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Inventory(
    @Json(name = "category")
    val category: String? = null,
    @Json(name = "category_description")
    val categoryDescription: String? = null,
    @Json(name = "ccf")
    val ccf: Int? = null,
    @Json(name = "consignment")
    val consignment: Boolean? = null,
    @Json(name = "costtype")
    val costtype: String? = null,
    @Json(name = "costtype_description")
    val costtypeDescription: String? = null,
    @Json(name = "deliverytime")
    val deliverytime: Int? = null,
    @Json(name = "hardresissue")
    val hardresissue: Boolean? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "internal")
    val `internal`: Boolean? = null,
    @Json(name = "inventoryid")
    val inventoryid: Int? = null,
    @Json(name = "issue1yrago")
    val issue1yrago: Double? = null,
    @Json(name = "issue2yrago")
    val issue2yrago: Double? = null,
    @Json(name = "issue3yrago")
    val issue3yrago: Double? = null,
    @Json(name = "issueunit")
    val issueunit: String? = null,
    @Json(name = "issueytd")
    val issueytd: Double? = null,
    @Json(name = "localref")
    val localref: String? = null,
    @Json(name = "location")
    val location: String? = null,
    @Json(name = "maxlevel")
    val maxlevel: Double? = null,
    @Json(name = "minlevel")
    val minlevel: Double? = null,
    @Json(name = "orderqty")
    val orderqty: Double? = null,
    @Json(name = "orderunit")
    val orderunit: String? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "reorder")
    val reorder: Boolean? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "siteid")
    val siteid: String? = null,
    @Json(name = "status")
    val status: String? = null,
    @Json(name = "status_description")
    val statusDescription: String? = null,
    @Json(name = "statusdate")
    val statusdate: String? = null
)