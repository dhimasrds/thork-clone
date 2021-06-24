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

fun main () {
    val dateString = "2020-11-23T23:05:59+07:00"
    val date = DateUtils.convertStringToMaximoDate(dateString)
    println(dateString)
    println(date)
}