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

package id.thork.app.helper

import okhttp3.RequestBody

class DoclinksParam constructor(
    var cookie: String? = null,
    var woId: Int? = null,
    var fileName: String? = null,
    var fileType: String? = null,
    var description: String? = null,
    var mimeType: String? = null,
    var requestBody: RequestBody? = null
)