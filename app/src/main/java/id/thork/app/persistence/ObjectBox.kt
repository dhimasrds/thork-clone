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

package id.thork.app.persistence

import android.content.Context
import android.util.Log
import id.thork.app.BuildConfig
import id.thork.app.base.BaseApplication
import id.thork.app.persistence.model.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import io.objectbox.sync.Sync

object ObjectBox {
    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()

        if (BuildConfig.DEBUG) {
            var syncAvailable = if (Sync.isAvailable()) "available" else "unavailable"
            Log.d(BaseApplication.TAG,
                "Using ObjectBox ${BoxStore.getVersion()} (${BoxStore.getVersionNative()}, sync $syncAvailable)")
            // Enable Data Browser on debug builds.
            // https://docs.objectbox.io/data-browser
            AndroidObjectBrowser(boxStore).start(context.applicationContext)
        }

    }

}