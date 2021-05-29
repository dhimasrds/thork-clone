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

import id.thork.app.helper.CookieHelper
import kotlinx.coroutines.*

fun main() {
    val list: List<String> = listOf("A", "B", "C")
    runBlocking {
        println("start")
        launch {
            println("another")
            delay(5000)
        }
        callA("HAI")
        callAnotherCoroutine("Hai2")
        println("run")
    }
    println("finish")
}

fun callA(a: String) {
    GlobalScope.launch(Dispatchers.IO) {
        delay(10000)
        println(a)
    }
}


fun callAnotherCoroutine(a: String) {
    runBlocking {
        delay(1000)
        println(a)
    }

}