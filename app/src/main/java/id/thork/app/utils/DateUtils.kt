package id.thork.app.utils

import id.thork.app.base.BaseParam
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Raka Putra on 1/20/21
 * Jakarta, Indonesia.
 */
object DateUtils {
    private val DEFAULT_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss"
    private val DATE_FORMAT = "dd-MMM-yyyy HH:mm"
    private val DATE_FORMAT_MAXIMO = "yyyy-MM-dd'T'HH:mm:ss"
    private val DATE_FORMAT_OBJECTBOX = "EEE MMM dd yyyy HH:mm:ss 'GMT'Z"

    /**
     * Get Time Interval from start date and end date
     *
     * @param startDate
     * @param endDate
     * @return
     */
    fun getTimeIntervalByDate(startDate: Date, endDate: Date): Double {
        return Math.floor((startDate.time - endDate.time).toDouble())
    }

    /**
     * Get Time Interval from start date (millisecond) and end date (millisecond)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    fun getTimeInterval(startTime: Long, endTime: Long): Double {
        return Math.floor((endTime - startTime).toDouble())
    }


    fun getTimestap(): String? {
        val timestamp = Timestamp(System.currentTimeMillis())
        return timestamp.time.toString()
    }

    fun getDateTime(): String? {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(DATE_FORMAT)
        return sdf.format(c.time)
    }

    fun getDateTimeMaximo(): String? {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(DATE_FORMAT_MAXIMO)
        return sdf.format(c.time)
    }

    fun getDateTimeOB(date: Date?): String? {
        try {
            val formatter = SimpleDateFormat(DATE_FORMAT_OBJECTBOX)
            formatter.applyPattern(DEFAULT_DATE_FORMAT)
            return formatter.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BaseParam.APP_EMPTY_STRING
    }

    fun convertDateFormat(dateString: String?): String? {
        try {
            val formatter = SimpleDateFormat(BaseParam.REPORT_DATE_FORMAT)
            val date = formatter.parse(dateString)
            val dateFormater: DateFormat = SimpleDateFormat(DATE_FORMAT)
            return dateFormater.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BaseParam.APP_EMPTY_STRING
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