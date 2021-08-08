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

package id.thork.app.example

import id.thork.app.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

fun main () {
    val tsLong = System.currentTimeMillis()
    println(tsLong)
    val tsLongs = tsLong / 1000
    println(tsLongs)
    val tsInt = tsLong.toInt()
    println(tsInt)

    //TIME SAMPLE
//    val timezone: TimeZone = TimeZone.getTimeZone("GMT+8")
//    val format = SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy")
//    format.timeZone = timezone
//    val cal1: Calendar = Calendar.getInstance()
//    println(cal1.time)
//    val dfCal1 = format.format(cal1.time)
//    println(dfCal1)

//    val dateString = "2020-11-23T23:05:59+07:00"
//    val date = DateUtils.convertStringToMaximoDate(dateString)
//    println(dateString)
//    println(date)
}