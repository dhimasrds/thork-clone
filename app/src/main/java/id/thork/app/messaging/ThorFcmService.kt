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
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.base.BaseParam
import id.thork.app.di.module.AppSession
import id.thork.app.workmanager.WorkerCoordinator
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ThorFcmService : FirebaseMessagingService() {
    private val TAG = ThorFcmService::class.java.name

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var workerCoordinator: WorkerCoordinator

    @Inject
    lateinit var appSession: AppSession

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag(TAG).i("onNewToken() token: %s context: %s", token, context)

        //subcribe topic 'thor-notif'
        FirebaseMessaging.getInstance().subscribeToTopic(BaseParam.FIREBASE_NOTIFICATION_TOPIC)
            .addOnSuccessListener {
                Timber.tag(
                    TAG
                ).i("onNewToken() subcribe on NOTIFICATION topic: %s", "success")
            }

        FirebaseMessaging.getInstance().subscribeToTopic(BaseParam.FIREBASE_LOCATION_TOPIC)
            .addOnSuccessListener {
                Timber.tag(
                    TAG
                ).i("onNewToken() subcribe on LOCATION topic: %s", "success")
            }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.tag(TAG).i(
            "onMessageReceived() remote Message: %s from: %s to: %s",
            remoteMessage,
            remoteMessage.from,
            remoteMessage.to
        )
        appSession.let {
            appSession.reinitUser()
        }
        remoteMessage.data.let {
            processingRemoteMessage(remoteMessage)
        }
    }

    private fun processingRemoteMessage(remoteMessage: RemoteMessage) {
        Timber.tag(TAG).i("processingRemoteMessage() topic: %s", remoteMessage.from)
        val crewId = remoteMessage.data.get("crewId")
        val isCrewExists = appSession.personUID.toString().equals(crewId)
        val isNotifTopic = remoteMessage.from == BaseParam.FIREBASE_TOPIC + BaseParam.FIREBASE_NOTIFICATION_TOPIC
        val isLocationTopic = remoteMessage.from == BaseParam.FIREBASE_TOPIC + BaseParam.FIREBASE_LOCATION_TOPIC

        Timber.tag(TAG).i("processingRemoteMessage() isNotifTopic: %s", isNotifTopic)
        Timber.tag(TAG).i("processingRemoteMessage() isLocationTopic: %s", isLocationTopic)

        if (isNotifTopic) {
            if (!crewId.isNullOrEmpty()) {
                Timber.tag(TAG).i(
                    "processingRemoteMessage() local crewId: %s server CrewId: %s Crew Exists: %s",
                    appSession.personUID, crewId, isCrewExists
                )
                if (isCrewExists) {
                    workerCoordinator.sendPushNotification(remoteMessage.data)
                }
            }
            //if (isBackgroundRunning(applicationContext)) {
            //notification when application on background
            //} else {
            //notification when application on foreground
            //}
        } else if (isLocationTopic) {
            Timber.tag(TAG).i("processingRemoteMessage location: %s", remoteMessage.toString())

            if (isBackgroundRunning(applicationContext)) {
                //firebase location when application on background
            }
            else if (!isCrewExists) {
                //notification when application on foreground
                workerCoordinator.addCrewPositionQueue(remoteMessage.data)
            }
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