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
import id.thork.app.persistence.entity.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import timber.log.Timber

object ObjectBox {
    lateinit var boxStore: BoxStore

    fun init(context: Context) {
        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()

        if (BuildConfig.DEBUG) {
            Timber.tag(BaseApplication.TAG).i(
                "Started..: %s ObjectBox version: %s",
                true,
                BoxStore.getVersion() + " (" + BoxStore.getVersionNative() + ")"
            )
            // Enable Data Browser on debug builds.
            // https://docs.objectbox.io/data-browser
            val started = AndroidObjectBrowser(boxStore).start(context.applicationContext)
            Log.d("Objectbox here", started.toString())
            Timber.tag(BaseApplication.TAG).i("init() object browser started: %s", started)
        }
    }

}