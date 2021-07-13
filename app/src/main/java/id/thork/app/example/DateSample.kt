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

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

fun main() {
    runBlocking {
        val lastUpdate = Date()
        val lastUpdateLong = lastUpdate.time
        println(lastUpdate)
        println(lastUpdateLong)
        delay(10000)

        val nextUpdate = Date()
        val nextUpdateLong = nextUpdate.time
        println(nextUpdate)
        println(nextUpdateLong)

    }



}