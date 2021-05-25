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

package id.thork.app.network.response.work_order.doclinks


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DescribedBy(
    @Json(name = "addinfo")
    val addinfo: Boolean? = null,
    @Json(name = "attachmentSize")
    val attachmentSize: Int? = null,
    @Json(name = "changeby")
    val changeby: String? = null,
    @Json(name = "copylinktowo")
    val copylinktowo: Boolean? = null,
    @Json(name = "createby")
    val createby: String? = null,
    @Json(name = "created")
    val created: String? = null,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "docType")
    val docType: String? = null,
    @Json(name = "docinfoid")
    val docinfoid: Int? = null,
    @Json(name = "fileName")
    val fileName: String? = null,
    @Json(name = "format")
    val format: Format? = null,
    @Json(name = "getlatestversion")
    val getlatestversion: Boolean? = null,
    @Json(name = "href")
    val href: String? = null,
    @Json(name = "identifier")
    val identifier: String? = null,
    @Json(name = "modified")
    val modified: String? = null,
    @Json(name = "ownerid")
    val ownerid: Int? = null,
    @Json(name = "ownertable")
    val ownertable: String? = null,
    @Json(name = "printthrulink")
    val printthrulink: Boolean? = null,
    @Json(name = "show")
    val show: Boolean? = null,
    @Json(name = "title")
    val title: String? = null,
    @Json(name = "upload")
    val upload: Boolean? = null,
    @Json(name = "urlType")
    val urlType: String? = null
)