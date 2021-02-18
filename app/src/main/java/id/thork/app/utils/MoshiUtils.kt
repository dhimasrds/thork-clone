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

package id.thork.app.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

object MoshiUtils {
    fun mapToJson(data: MutableMap<String, String>): String? {
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
        val jsonString = moshi.adapter<Map<String, Any>>(type)
            .toJson(data)
        return jsonString
    }
}