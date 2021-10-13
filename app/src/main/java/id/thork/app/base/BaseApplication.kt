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
package id.thork.app.base

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.flurry.android.FlurryAgent
import dagger.hilt.android.HiltAndroidApp
import id.thork.app.initializer.ObjectBox
import id.thork.app.initializer.TimberInitializer
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider{
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    companion object Constants {
        const val TAG = "This-FSM"
        lateinit var context: Context

        fun getAppContext(): Context {
            return context
        }
    }

    override fun onCreate() {
        // Optional: if you distribute your app as App Bundle, provides detection of incomplete
        // installs due to sideloading and helps users reinstall the app from Google Play.
        // https://docs.objectbox.io/android/app-bundle-and-split-apk
//        if (MissingSplitsManagerFactory.create(this).disableAppIfMissingRequiredSplits()) {
//            return // Skip app initialization.
//        }

        super.onCreate()
        context = applicationContext
        FlurryAgent.Builder()
            .withLogEnabled(true)
            .build(this, "5GZ8RX8DRFVJ2SJWMBFW")
        TimberInitializer.init()
        ObjectBox.init(this)
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

}