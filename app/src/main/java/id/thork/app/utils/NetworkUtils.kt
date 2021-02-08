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

import id.thork.app.helper.ConnectionState
import timber.log.Timber
import kotlin.math.round

object NetworkUtils {
    private val TAG = NetworkUtils::class.java.name

    private const val POOR_BANDWIDTH = 100
    private const val AVERAGE_BANDWIDTH = 550
    private const val GOOD_BANDWIDTH = 2000

    fun getDownloadSpeedByInterval(fileSize: Long, startTime: Long, endTime: Long): Int {
        val timeTakenMills = DateUtils.getTimeInterval(startTime, endTime)
        val timeTakensSec = timeTakenMills / 1000
        val kilobytesPerSec = round(fileSize / timeTakensSec)
        return kilobytesPerSec.toInt()
    }

    fun getConnectionState(startTime: Long, endTime: Long): Int {
        val kilobytesPerSec = getDownloadSpeedByInterval(1024, startTime, endTime)
        Timber.tag(TAG).i(
            "getConnectionState() start time: %s end time: %s kilobytesPerSec: %s",
            startTime, endTime,
            kilobytesPerSec
        )
        return when {
            (kilobytesPerSec < AVERAGE_BANDWIDTH) -> ConnectionState.SLOW.state
            (kilobytesPerSec < GOOD_BANDWIDTH) -> ConnectionState.NORMAL.state
            (kilobytesPerSec >= GOOD_BANDWIDTH) -> ConnectionState.GOOD.state
            else -> ConnectionState.DISCONNECT.state
        }
    }
}