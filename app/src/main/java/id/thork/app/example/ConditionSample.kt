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

import java.util.concurrent.ThreadLocalRandom

fun main() {
//    val current = System.currentTimeMillis()
//    println(current)
//
//    val angka: Int =ThreadLocalRandom.current().nextInt()
//    println(angka)
//
//    val a = if (1 == 1) {
//        true
//    } else {
//        false
//    }
//    println(a)
//
//    val b = true
//    if (!b) {
//        println(b)
//    }

    val uriString = "content://com.android.providers.downloads.documents/document/raw%3A%2Fstorage%2Femulated%2F0%2FDownload%2FAndroid%20Resource%20Naming%20Convention.pdf"
    val bool = uriString.contains("raw")
    println(bool)
}