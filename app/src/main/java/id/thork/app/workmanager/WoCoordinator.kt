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
import androidx.work.*
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class WoCoordinator @Inject constructor(
    val context: Context) {
    private val TAG = WoCoordinator::class.java.name
    fun ping() {
        Timber.tag(TAG).i("ping()")
    }

    fun addQueue(wonum: String, woid: Int) {
        //Work manager only execute when connected to internet
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workerId = UUID.randomUUID().toString()
        val inputData =
            workDataOf("wonum" to "$wonum", "woid" to "$woid", "workderid" to "$workerId")
        //backoff criteria for Retry work manager if work is need to retry
        val syncWorkOrderRequest: WorkRequest = OneTimeWorkRequestBuilder<WoWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()
        WorkManager.getInstance(context).enqueue(syncWorkOrderRequest)
    }
}