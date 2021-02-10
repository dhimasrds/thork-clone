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
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber

class PushNotificationWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    private val TAG = PushNotificationWorker::class.java.name

    private val MAX_RUN_ATTEMPT = 6
    override fun doWork(): Result {
        try {
            if (runAttemptCount > MAX_RUN_ATTEMPT) {
                Result.failure()
            }

            //If Post to server success then return result.success
            //Else return result.retry until MAX RUN ATTEMPT
            val data  = inputData.getString("data")
            val angka  = inputData.getInt("angka", 44)
            val outputData = Data.Builder()
                .putString("data", data)
                .putInt("angka", angka)
                .build()

            Timber.tag(TAG).i("doWork() sync data: %s angka: %s", data, angka)
            return Result.success(outputData)
        } catch (e: Exception) {
            Timber.tag(TAG).e("doWork() error: %s", e.message)
            return Result.retry()
        }
    }
}