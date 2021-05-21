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
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import id.thork.app.base.BaseParam
import id.thork.app.di.module.AppResourceMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.AssetDao
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.repository.WorkOrderRepository
import id.thork.app.repository.WorkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class WorkOrderWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    val appSession: AppSession,
    val preferenceManager: PreferenceManager,
    val mx: AppResourceMx,
    val httpLoggingInterceptor: HttpLoggingInterceptor,
    val woCacheDao: WoCacheDao,
    val assetDao: AssetDao
) :
    Worker(context, workerParameters) {
    private val TAG = WorkOrderWorker::class.java.name

    var workOrderRepository: WorkOrderRepository
    var response = WorkOrderResponse()

    init {
        val workerRepository =
            WorkerRepository(
                preferenceManager,
                httpLoggingInterceptor,
                woCacheDao,
                appSession,
                assetDao
            )
        workOrderRepository = workerRepository.buildWorkorderRepository()
        Timber.tag(TAG).i("WorkOrderWorker() workOrderRepository: %s", workOrderRepository)
    }


    private val MAX_RUN_ATTEMPT = 6
    override fun doWork(): Result {
        try {
            Timber.tag(TAG).d("WorkOrderWorker() username: %s", appSession.personUID)
            Timber.tag(TAG).d(
                "WorkOrderWorker() sharedPreferences: %s",
                preferenceManager.getString(BaseParam.APP_MX_COOKIE)
            )
            Timber.tag(TAG).d("WorkOrderWorker() sharedPreferences: %s", mx.fsmResWorkorder)

            //Query Local WO Record is needed to sync with the server
            if (runAttemptCount > MAX_RUN_ATTEMPT) {
                Result.failure()
            }

            //TODO Execute Sync WO

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
    private fun syncUpdateWo() {
        //TODO Query to local check wo cache offline

    }

    fun updateStatusWo(
        woId: Int?,
        status: String?,
        wonum: String?,
        longdesc: String?,
        nextStatus: String
    ) {

        workOrderRepository.updateWoCacheBeforeSync(woId, wonum, status, longdesc, nextStatus)

        val member = Member()
        member.status = nextStatus
        member.descriptionLongdescription = longdesc

        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType: String = ("application/json")
        GlobalScope.launch(Dispatchers.IO) {
            if (woId != null) {
                workOrderRepository.updateStatus(cookie,
                    xMethodeOverride,
                    contentType,
                    woId,
                    member,
                    onSuccess = {
                        workOrderRepository.updateWoCacheAfterSync(wonum, longdesc, nextStatus)
                    },
                    onError = {
                        Timber.tag(TAG).i("onError() onError: %s", it)
                    })
            }
        }
    }



}