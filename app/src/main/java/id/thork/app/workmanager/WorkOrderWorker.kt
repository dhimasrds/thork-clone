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
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class WorkOrderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters

) :
    Worker(context, workerParameters) {
    private val TAG = WorkOrderWorker::class.java.name

    private val MAX_RUN_ATTEMPT = 6
    override fun doWork(): Result {
        try {
            //Query Local WO Record is needed to sync with the server
            if (runAttemptCount > MAX_RUN_ATTEMPT) {
                Result.failure()
            }

            //If Post to server success then return result.success
            //Else return result.retry until MAX RUN ATTEMPT
            val workerId = inputData.getString("workerid")
            val wonum = inputData.getString("wonum")
            val woid = inputData.getInt("woid", 0)
            Timber.tag(TAG).i(
                "WorkOrderWorker() doWork() sync workerId: %s wonum: %s woid: %s",
                workerId, wonum, woid
            )
            return Result.success()
        } catch (e: Exception) {
            Timber.tag(TAG).e("doWork() error: %s", e.message)
            return Result.retry()
        }
    }


    //TODO http request

}