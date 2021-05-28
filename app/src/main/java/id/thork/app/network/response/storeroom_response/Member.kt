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

package id.thork.app.network.response.storeroom_response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Member(
    @Json(name = "autowogen")
    val autowogen: Boolean? = null,
    @Json(name = "changeby")
    val changeby: String? = null,
    @Json(name = "changedate")
    val changedate: String? = null,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "disabled")
    val disabled: Boolean? = null,
    @Json(name = "expectedlife")
    val expectedlife: Int? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "inventory")
    val inventory: List<Inventory>? = null,
    @Json(name = "inventory_collectionref")
    val inventoryCollectionref: String? = null,
    @Json(name = "isdefault")
    val isdefault: Boolean? = null,
    @Json(name = "isrepairfacility")
    val isrepairfacility: Boolean? = null,
    @Json(name = "location")
    val location: String? = null,
    @Json(name = "locationsid")
    val locationsid: Int? = null,
    @Json(name = "orgid")
    val orgid: String? = null,
    @Json(name = "pluscloop")
    val pluscloop: Boolean? = null,
    @Json(name = "pluscpmextdate")
    val pluscpmextdate: Boolean? = null,
    @Json(name = "replacecost")
    val replacecost: Double? = null,
    @Json(name = "_rowstamp")
    val rowstamp: String? = null,
    @Json(name = "siteid")
    val siteid: String? = null,
    @Json(name = "status")
    val status: String? = null,
    @Json(name = "status_description")
    val statusDescription: String? = null,
    @Json(name = "statusdate")
    val statusdate: String? = null,
    @Json(name = "thisfsmtagprogress")
    val thisfsmtagprogress: Boolean? = null,
    @Json(name = "type")
    val type: String? = null,
    @Json(name = "type_description")
    val typeDescription: String? = null,
    @Json(name = "useinpopr")
    val useinpopr: Boolean? = null
)