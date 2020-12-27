///*
// * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved.
// *
// * This software is the confidential and proprietary information of
// * This.ID. ("Confidential Information").
// *
// * Such Confidential Information shall not be disclosed and shall
// * use it only	 in accordance with the terms of the license agreement
// * entered into with This.ID; other than in accordance with the written
// * permission of This.ID.
// */
//
//package id.thork.app.utils
//
//import com.squareup.moshi.JsonAdapter
//import com.squareup.moshi.Moshi
//import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
//import java.util.*
//
//
//object MoshiUtils {
//    val moshi = Moshi.Builder()
//        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()
//
//    fun main
//    fun toJson(@Nullable value: T?): String {}
//
//
//    fun <T> adapter(type: Class<T>?): JsonAdapter<T>? {
//        return adapter(type, Util.NO_ANNOTATIONS)
//    }
//
//    fun <T> toJson(type: Class<T>, ss: T): String {
//        val jsonAdapter: JsonAdapter<out Class<out T>> = moshi.adapter(type::class.java)
//        return jsonAdapter.toJson(ss)
//    }
//
//    fun fromJson() {
//
//    }
//}