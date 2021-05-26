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
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.di.module.AppResourceMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.api.DoclinksClient
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.AssetDao
import id.thork.app.persistence.dao.AttachmentDao
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.WorkOrderRepository
import id.thork.app.repository.WorkerRepository
import id.thork.app.utils.WoUtils
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
    val assetDao: AssetDao,
    val attachmentDao: AttachmentDao,
    val doclinksClient: DoclinksClient,
//    val workOrderAdapter: WorkOrderAdapter
) :
    Worker(context, workerParameters) {
    private val TAG = WorkOrderWorker::class.java.name

    var workOrderRepository: WorkOrderRepository
    var response = WorkOrderResponse()

    init {
        val workerRepository =
            WorkerRepository(
                context,
                preferenceManager,
                httpLoggingInterceptor,
                woCacheDao,
                appSession,
                assetDao,
                attachmentDao,
                doclinksClient
            )
        workOrderRepository = workerRepository.buildWorkorderRepository()
        Timber.tag(TAG).i("WorkOrderWorker() workOrderRepository: %s", workOrderRepository)
    }


    private val MAX_RUN_ATTEMPT = 6
    private var syncUpdateSuccess = false

    override fun doWork(): Result {
        try {
            //Query Local WO Record is needed to sync with the server
            if (runAttemptCount > MAX_RUN_ATTEMPT) {
                Result.failure()
            }

            syncUpdateWo()
            return Result.success()

        } catch (e: Exception) {
            Timber.tag(TAG).e("doWork() error: %s", e.message)
            return Result.retry()
        }
    }


    //TODO http request
    private fun syncUpdateWo() {
        //TODO Query to local check wo cache offline
        val woCacheList =
            workOrderRepository.fetchWoListOffline(BaseParam.APP_FALSE, BaseParam.APP_TRUE)
        val index = 0
        woCacheList.whatIfNotNullOrEmpty {
            Timber.d("syncUpdateWo() wo list size %s", it.size)
            updateStatusWoOffline(it, index)
        }

    }

    fun updateStatusWoOffline(
        listWo: List<WoCacheEntity>,
        currentIndex: Int,
    ) {
        val currentWo = listWo.get(currentIndex)
        currentWo.whatIfNotNull {
            val prepareBody = WoUtils.convertBodyToMember(it.syncBody.toString())

            prepareBody.whatIfNotNull { prepareBody ->
                val woId = prepareBody.workorderid
                val wonum = prepareBody.wonum
                val longdesc = prepareBody.longdescription?.get(0)?.ldtext
                val status = prepareBody.status

                val member = Member()
                member.status = status
                member.descriptionLongdescription = longdesc

                Timber.tag(TAG).d(
                    "updateStatusWoOffline() updateWo() woId %s, wonum %s, longdesc %s, status %s",
                    woId,
                    wonum,
                    longdesc,
                    status
                )

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
                                workOrderRepository.updateWoCacheAfterSync(
                                    wonum,
                                    longdesc,
                                    status.toString()
                                )
                                val nextIndex = currentIndex + 1
                                if (nextIndex <= listWo.size - 1) {
                                    updateStatusWoOffline(listWo, nextIndex)
                                } else {
//                                    workOrderAdapter.refresh()
                                    syncUpdateSuccess = true
                                    Timber.tag(TAG).i("WorkOrderWorker() syncUpdateSuccess %s",syncUpdateSuccess)
                                }
                            },
                            onError = {
                                Timber.tag(TAG).i("onError() onError: %s", it)
                            })
                    }
                }
            }
        }
    }
}