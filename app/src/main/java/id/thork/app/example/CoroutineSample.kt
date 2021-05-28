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

import kotlinx.coroutines.*

fun main() {
    val list: List<String> = listOf("A", "B", "C")
//    list.forEach {
        println("Start")
//        GlobalScope.launch(Dispatchers.IO) {
//            callA(it)
//        }

        runBlocking {
            launch {
                delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
                println("World!") // print after delay
            }
            println("Hello") // main coroutine continues while a previous one is delayed
        }
        println("Finish")
//    }

}

fun callA(a: String) {
    GlobalScope.launch(Dispatchers.IO) {
        println(a)
    }
}