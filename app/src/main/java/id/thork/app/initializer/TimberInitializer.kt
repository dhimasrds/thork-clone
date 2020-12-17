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
import id.thork.app.BuildConfig
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

        } else {
            Timber.plant(TimberReleaseTree())
        }
    }

}