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

package id.thork.app.messaging

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent
import id.thork.app.di.module.PushNotificationLiveData
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ThorFcmService: FirebaseMessagingService() {
    private val TAG = ThorFcmService::class.java.name

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var pushNotificationLiveData: PushNotificationLiveData

    override fun onCreate() {
        super.onCreate()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag(TAG).i("onNewToken() token: %s context: %s", token, context)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        processingRemoteMessage(remoteMessage)
        Timber.tag(TAG).i("onMessageReceived() remote Message: %s context: %s", remoteMessage, context)
    }

    private fun processingRemoteMessage(remoteMessage: RemoteMessage) {
        if (isBackgroundRunning(applicationContext)) {
            Timber.tag(TAG).i("processingRemoteMessage() on background true");
            //push notification on background with workmanager
        } else {
            Timber.tag(TAG).i("processingRemoteMessage() on foreground true");
            //push notification on foreground with live data
            pushNotificationLiveData.onMessageReceived(remoteMessage)
        }
    }

    fun isBackgroundRunning(context: Context): Boolean {
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == context.packageName) {
                        //If your app is the process in foreground, then it's not in running in background
                        return false
                    }
                }
            }
        }
        return true
    }
}