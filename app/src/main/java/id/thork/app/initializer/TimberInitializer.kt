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

package id.thork.app.initializer

import android.content.Context
import android.util.Log
import id.thork.app.BuildConfig
import id.thork.app.base.BaseApplication
import io.objectbox.BoxStore
import timber.log.Timber
import timber.log.Timber.DebugTree


object TimberInitializer {

    fun init(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return super.createStackElementTag(element) + " >> " + element.lineNumber
                }
            })
            Timber.tag(BaseApplication.TAG).i(
                "Started..: %s ObjectBox version: %s",
                true,
                BoxStore.getVersion() + " (" + BoxStore.getVersionNative() + ")"
            )
        } else {
            Timber.plant(ReleaseTree())
        }
    }

}