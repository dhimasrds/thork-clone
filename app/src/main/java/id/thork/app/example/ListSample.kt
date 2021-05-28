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

fun main() {
    val attachmentEntities: MutableList<Int> = mutableListOf(1,2,3)
    println("a1 $attachmentEntities" )
    attachmentEntities.add(5)
    println("a2 $attachmentEntities" )
}
