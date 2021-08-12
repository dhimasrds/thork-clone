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
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.ApiParam
import id.thork.app.network.api.DoclinksClient
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.*
import id.thork.app.repository.*
import id.thork.app.utils.MoshiUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class WorkerCoordinator @Inject constructor(
    val context: Context,
    val preferenceManager: PreferenceManager,
    val httpLoggingInterceptor: HttpLoggingInterceptor,
    val woCacheDao: WoCacheDao,
    val appSession: AppSession,
    val assetDao: AssetDao,
    val attachmentDao: AttachmentDao,
    val doclinksClient: DoclinksClient,
    val materialBackupDao: MaterialBackupDao,
    val matusetransDao: MatusetransDao,
    val wpmaterialDao: WpmaterialDao,
    val materialDao: MaterialDao,
    val worklogDao: WorklogDao,
    val worklogTypeDao: WorklogTypeDao,
    val taskDao: TaskDao,
    val laborPlanDao: LaborPlanDao,
    val laborActualDao: LaborActualDao,
    val laborMasterDao: LaborMasterDao,
    val craftMasterDao: CraftMasterDao
) {
    private val TAG = WorkerCoordinator::class.java.name

    var workOrderRepository: WorkOrderRepository
    var attachmentRepository: AttachmentRepository
    var materialRepository: MaterialRepository
    var worklogRepository: WorklogRepository
    var taskRepository: TaskRepository
    var laborRepository: LaborRepository
    var response = WorkOrderResponse()

    //Work manager only execute when connected to internet
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

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
                materialBackupDao,
                matusetransDao,
                wpmaterialDao,
                materialDao,
                worklogDao,
                worklogTypeDao,
                taskDao,
                laborPlanDao, laborActualDao, laborMasterDao, craftMasterDao
            )
        workOrderRepository = workerRepository.buildWorkorderRepository()
        attachmentRepository = workerRepository.buildAttachmentRepository()
        materialRepository = workerRepository.buildMaterialRepository()
        worklogRepository = workerRepository.buildWorklogRepository()
        taskRepository = workerRepository.buildTaskRepository()
        laborRepository = workerRepository.buildLaborRepository()

        Timber.tag(TAG).i("WorkerCoordinator() workOrderRepository: %s", workOrderRepository)
    }

    fun ping() {
        Timber.tag(TAG).i("ping()")
    }

    fun addSyncWoQueue() {
        val SYNC_WO = "SYNC_WO"
//        val workerId = UUID.randomUUID().toString()
//        val inputData =
//            workDataOf("wonum" to "$wonum", "woid" to "$woid", "workerid" to "$workerId")
        //backoff criteria for Retry work manager if work is need to retry
        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<WorkOrderWorker>()
            .addTag(SYNC_WO)
//            .setInputData(inputData)
            .setConstraints(constraints)
            .setInitialDelay(15, TimeUnit.SECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(workRequest)

//        val outputWorkInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData("")
//        val workInfo = outputWorkInfos.value?.get(0)
    }

    fun addSyncTask() {
        val SYNC_TASK = "SYNC_TASK"

        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<TaskWorker>()
            .addTag(SYNC_TASK)
            .setConstraints(constraints)
            .setInitialDelay(18, TimeUnit.SECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(workRequest)
    }

    fun addSyncAttendance() {
        val SYNC_ATTENDANCE = "SYNC_ATTENDANCE"

        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<AttendanceWorker>()
            .addTag(SYNC_ATTENDANCE)
            .setConstraints(constraints)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(workRequest)
    }

    fun addCrewPositionQueue(remoteMessageMap: MutableMap<String, String>) {
        val CREW_POSITION = "CREW_POSITION"
        val crewId = remoteMessageMap.get("crewId")
        val laborcode = remoteMessageMap.get("laborcode")
        val longitude = remoteMessageMap.get("longitude")
        val latitude = remoteMessageMap.get("latitude")
        val tag = remoteMessageMap.get("tag")

        Timber.tag(TAG).i("addCrewPositionQueue() data : %s", remoteMessageMap)
        val inputData = workDataOf(
            "crewId" to "$crewId", "laborcode" to "$laborcode",
            "longitude" to "$longitude", "latitude" to "$latitude",
            "tag" to "$tag"
        )

        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<LocationWorker>()
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
        Timber.tag(TAG).i(
            "receivePushNotification() remote map: %s remote map json: %s",
            remoteMessageMap, remoteMessageString
        )

        val data = JSONObject(remoteMessageString)
        val wonum = data.getString("wonum")
        val workorderid = data.getString("workorderid")
        Timber.tag(TAG).i("receivePushNotification() wonum: $wonum")

        val wocache = workOrderRepository.findWobyWonum(wonum)

        wocache.whatIfNotNull(
            whatIf = {
                Timber.d("sendPushNotification() wocache available")
                remoteMessageString.whatIfNotNullOrEmpty {
                    generatePushNotificationWorker(it)
                }
            },
            whatIfNot = {
                remoteMessageString.whatIfNotNullOrEmpty {
                    searchWoFromServer(workorderid.toInt(), it)
                }
            }
        )
    }

    private fun searchWoFromServer(workorderid: Int, remoteMessageString: String) {
        val laborcode: String? = appSession.laborCode
        val select: String = ApiParam.API_SELECT_ALL
        val where: String =
            ApiParam.WORKORDER_WHERE_LABORCODE_NEW + "\"" + laborcode + "\"" + ApiParam.WORKORDER_WHERE_WOID + workorderid

        GlobalScope.launch {
            appSession.userHash.whatIfNotNullOrEmpty {
                workOrderRepository.searchWorkOrder(
                    it, select, where,
                    onSuccess = {
                        response = it
                        Timber.d("searchWoFromServer :%s", it.member)

                        response.member.whatIfNotNullOrEmpty(
                            whatIf = {
                                workOrderRepository.addWoToObjectBox(response.member!!)
                                generatePushNotificationWorker(remoteMessageString)
                            }
                        )
                    },
                    onError = {
                    },
                    onException = {
                    })
            }

        }
    }

    private fun generatePushNotificationWorker(remoteMessageString: String) {
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
