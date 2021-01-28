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

import kotlin.math.floor

object DateUtils {
    fun getTimeInterval(startTime:Long, endTime: Long): Double {
        val interval = endTime - startTime
        return floor(interval.toDouble())
    }
}