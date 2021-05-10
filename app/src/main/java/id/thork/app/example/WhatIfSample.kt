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

import com.skydoves.whatif.whatIf
import com.skydoves.whatif.whatIfAnd
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty

fun main() {

    println("Hei")
    validateIsNotNullOrNotEmpty()
    validateBoolean()
    validateMultipleCondition()
}

fun validateIsNotNullOrNotEmpty() {
    val hello: String? = null
    hello.whatIfNotNullOrEmpty {
        println("hello is not null")
    }

    hello.whatIfNotNull(
        whatIf = { println("hello is not null") },
        whatIfNot = { println("hello is null or empty") }
    )
}

fun validateBoolean() {
    val bool: Boolean = true
    bool.whatIf(
        whatIf = { println("Bool is true") },
        whatIfNot = { println("Bool is false") }
    )
}
fun validateMultipleCondition() {
    val hello: String? = "hai"
    val gotcha: String? = null

    hello.isNullOrEmpty().whatIfAnd(gotcha.isNullOrEmpty()) {
        println("hello is null and empty also gotcha is null and empty ")
    }
}