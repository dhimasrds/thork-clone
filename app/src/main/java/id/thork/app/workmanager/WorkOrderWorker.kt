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
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import id.thork.app.base.BaseParam
import id.thork.app.di.module.AppResourceMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.api.DoclinksClient
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.*
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.*
import id.thork.app.utils.DateUtils
import id.thork.app.utils.WoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.*

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
    val materialBackupDao: MaterialBackupDao,
    val matusetransDao: MatusetransDao,
    val wpmaterialDao: WpmaterialDao,
    val materialDao: MaterialDao,
    val worklogDao: WorklogDao,
    val worklogTypeDao: WorklogTypeDao
) :
    Worker(context, workerParameters) {
    private val TAG = WorkOrderWorker::class.java.name

    var workOrderRepository: WorkOrderRepository
    var response = WorkOrderResponse()
    var attachmentRepository: AttachmentRepository
    var materialRepository: MaterialRepository
    var worklogRepository: WorklogRepository

    private lateinit var attachmentEntities: MutableList<AttachmentEntity>

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
                doclinksClient,
                materialBackupDao, matusetransDao, wpmaterialDao, materialDao, worklogDao,
                worklogTypeDao
            )
        workOrderRepository = workerRepository.buildWorkorderRepository()
        attachmentRepository = workerRepository.buildAttachmentRepository()
        materialRepository = workerRepository.buildMaterialRepository()
        worklogRepository = workerRepository.buildWorklogRepository()

        Timber.tag(TAG).i("WorkOrderWorker() workOrderRepository: %s", workOrderRepository)
    }


    private val MAX_RUN_ATTEMPT = 6

    override fun doWork(): Result {
        try {
            Timber.tag(TAG).d("doWork() workOrderWorker")
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
        Timber.tag(TAG).d("syncUpdateWo() syncUpdateWo")
        //TODO Query to local check wo cache offline
        val woCacheList =
            workOrderRepository.fetchWoListOffline(BaseParam.APP_FALSE, BaseParam.APP_TRUE)
        val index = 0
        val listWocache = mutableListOf<WoCacheEntity>()
        val listCreateWoOffline = mutableListOf<WoCacheEntity>()
        woCacheList.forEach {
            if (it.status.equals(BaseParam.WAPPR)) {
                listCreateWoOffline.add(it)
            } else {
                listWocache.add(it)
            }
        }

        listWocache.whatIfNotNullOrEmpty {
            Timber.d("syncUpdateWo() wo list size %s", it.size)
            updateStatusWoOffline(it, index)
        }

        listCreateWoOffline.whatIfNotNullOrEmpty {
            updateCreateWo(it, index)
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
                val listMatAct = materialRepository.prepareMaterialActual(woId, wonum)
                val listWorklog = worklogRepository.prepareBodyListWorklog(woId.toString(), BaseParam.APP_FALSE)
                val member = Member()
                member.status = status
                member.descriptionLongdescription = longdesc
                listMatAct.whatIfNotNullOrEmpty {
                    member.matusetrans = it
                }
                listWorklog.whatIfNotNullOrEmpty {
                    member.worklog = listWorklog
                }

                Timber.tag(TAG).d(
                    "updateStatusWoOffline() updateWo() woId %s, wonum %s, longdesc %s, status %s",
                    woId,
                    wonum,
                    longdesc,
                    status
                )
                woId.whatIfNotNull {
                    checklistAttachment(it)
                }

                val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
                val xMethodeOverride: String = BaseParam.APP_PATCH
                val contentType: String = ("application/json")
                val patchType: String = BaseParam.APP_MERGE
                runBlocking {
                    launch(Dispatchers.IO) {
                        if (woId != null) {
                            //TESTING
                            val moshi = Moshi.Builder()
                                .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()
                            val jsonAdapter: JsonAdapter<Member> = moshi.adapter(Member::class.java)
                            val jsonString = jsonAdapter.toJson(member)

                            Timber.tag(TAG).d("updateStatusWoOffline() woId: %s", woId)
                            Timber.tag(TAG).d("updateStatusWoOffline() jsonString: %s", jsonString)

                            workOrderRepository.updateStatus(cookie,
                                xMethodeOverride,
                                contentType,
                                patchType,
                                woId,
                                member,
                                onSuccess = {
                                    workOrderRepository.updateWoCacheAfterSync(
                                        woId, wonum,
                                        longdesc,
                                        status.toString()
                                    )
                                    materialRepository.checkMatActAfterUpdate(woId)
                                    worklogRepository.handlingAfterUpdate(woId.toString())

                                    val nextIndex = currentIndex + 1
                                    if (nextIndex <= listWo.size - 1) {
                                        updateStatusWoOffline(listWo, nextIndex)
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

    fun checklistAttachment(woId: Int) {
        attachmentEntities = attachmentRepository.getAttachmentByWoIdAndSyncStatus(woId, false)
        attachmentEntities.whatIfNotNullOrEmpty {
            GlobalScope.launch(Dispatchers.IO) {
                attachmentRepository.uploadAttachment(
                    it as MutableList<AttachmentEntity>,
                    appSession.userEntity.username.toString()
                )
            }
        }
    }

    fun updateCreateWo(
        listWo: List<WoCacheEntity>,
        currentIndex: Int,
    ) {
        val currentWo = listWo.get(currentIndex)

        currentWo.whatIfNotNull {
            val prepareBody = WoUtils.convertBodyToMember(it.syncBody.toString())

            prepareBody.whatIfNotNull { prepareBody ->
                val longdesc = prepareBody.longdescription?.get(0)?.ldtext
                val status = prepareBody.status

                val member = Member()
                member.siteid = appSession.siteId
                member.location = prepareBody.location
                member.assetnum = prepareBody.assetnum
                member.description = prepareBody.description
                member.status = status
                member.reportdate = DateUtils.getDateTimeMaximo()
                member.estdur = prepareBody.estdur
                member.wopriority = prepareBody.wopriority
                member.descriptionLongdescription = prepareBody.descriptionLongdescription
                prepareBody.origrecordid.whatIfNotNull {
                    member.origrecordid = it
                    member.origrecordclass = prepareBody.origrecordclass
                }
                prepareBody.wpmaterial.whatIfNotNullOrEmpty {
                    member.wpmaterial = it
                }

                val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
                val properties = BaseParam.APP_ALL_PROPERTIES
                GlobalScope.launch(Dispatchers.IO) {
                    workOrderRepository.createWo(
                        cookie, properties, member,
                        onSuccess = {
                            //TODO handle create wo cache after update
                            workOrderRepository.updateWoCacheAfterSync(
                                it.workorderid,
                                it.wonum,
                                longdesc,
                                status.toString()
                            )

                            val nextIndex = currentIndex + 1
                            if (nextIndex <= listWo.size - 1) {
                                updateCreateWo(listWo, nextIndex)
                            }
                        }, onError = {
                            Timber.tag(TAG).i("createWo() error: %s", it)
                        }
                    )
                }
            }
        }
    }
}