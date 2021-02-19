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

package id.thork.app.workmanager

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import com.google.firebase.messaging.RemoteMessage
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.thork.app.utils.MoshiUtils
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.HashMap

@Module
@InstallIn(SingletonComponent::class)
class WorkerCoordinator @Inject constructor(
    val context: Context
) {
    private val TAG = WorkerCoordinator::class.java.name

    //Work manager only execute when connected to internet
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun ping() {
        Timber.tag(TAG).i("ping()")
    }

    fun addSyncWoQueue(wonum: String, woid: Int) {
        val SYNC_WO = "SYNC_WO-"
        val workerId = UUID.randomUUID().toString()
        val inputData =
            workDataOf("wonum" to "$wonum", "woid" to "$woid", "workerid" to "$workerId")
        //backoff criteria for Retry work manager if work is need to retry
        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<WorkOrderWorker>()
            .addTag(SYNC_WO + workerId)
            .setInputData(inputData)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(workRequest)

        val outputWorkInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData("")
        val workInfo = outputWorkInfos.value?.get(0)
    }

    fun addCrewPositionQueue(remoteMessageMap: MutableMap<String, String>) {
        val CREW_POSITION = "CREW_POSITION"
        val title = remoteMessageMap.get("title")
        val message = remoteMessageMap.get("message")
        val crewId = remoteMessageMap.get("crewId")
        val laborcode = remoteMessageMap.get("laborcode")
        val longitude = remoteMessageMap.get("longitude")
        val latitude = remoteMessageMap.get("latitude")
        val tag = remoteMessageMap.get("tag")

        Timber.tag(TAG).i("addCrewPositionQueue() tag: %s", tag)
        val inputData = workDataOf(
            "title" to "$title", "message" to "$message",
            "crewId" to "$crewId", "laborcode" to "$laborcode",
            "longitude" to "$longitude", "latitude" to "$latitude",
            "tag" to "$tag",
        )
        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<PushNotificationWorker>()
            .addTag(CREW_POSITION)
            .setInputData(inputData)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun sendPushNotification(remoteMessageMap: MutableMap<String, String>) {
        val remoteMessageString = MoshiUtils.mapToJson(remoteMessageMap)
        Timber.tag(TAG).i("receivePushNotification() remote map: %s remote map json: %s",
            remoteMessageMap, remoteMessageString)
        val inputData = workDataOf("data" to remoteMessageString)
        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<PushNotificationWorker>()
            .addTag("PUSH_NOTIFICATION")
            .setInputData(inputData)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}