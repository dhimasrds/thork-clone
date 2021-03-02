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
import androidx.work.Worker
import androidx.work.WorkerParameters
import id.thork.app.helper.NotificationData
import id.thork.app.utils.NotificationUtils
import timber.log.Timber

class PushNotificationWorker(val context: Context, val workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    private val TAG = PushNotificationWorker::class.java.name

    private val MAX_RUN_ATTEMPT = 6
    override fun doWork(): Result {
        try {
            if (runAttemptCount > MAX_RUN_ATTEMPT) {
                Result.failure()
            }

            val data  = inputData.getString("data")
            if (!data.isNullOrEmpty()) {
                val notificationUtils = NotificationUtils(context)
                val notificationData = NotificationData("PUSH_NOTIFICATION", data)
                notificationUtils.sendNotification(notificationData)
                Timber.tag(TAG).i("doWork() receive notification data: %s",data)

                return Result.success()
            }
            return Result.retry()


            //            val angka  = inputData.getInt("angka", 44)
//            val outputData = Data.Builder()
//                .putString("data", data)
//                .putInt("angka", angka)
//                .build()
//            return Result.success(outputData)

        } catch (e: Exception) {
            Timber.tag(TAG).e("doWork() error: %s", e.message)
            return Result.retry()
        }
    }
}