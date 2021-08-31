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
import id.thork.app.persistence.dao.*
import id.thork.app.persistence.entity.LaborPlanEntity
import id.thork.app.repository.LaborRepository
import id.thork.app.repository.TaskRepository
import id.thork.app.repository.WorkOrderRepository
import id.thork.app.repository.WorkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

/**
 * Created by M.Reza Sulaiman on 27/08/2021
 * Jakarta, Indonesia.
 */
class LaborWorker @WorkerInject constructor(
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
    val storeroomDao: StoreroomDao,
    val worklogDao: WorklogDao,
    val worklogTypeDao: WorklogTypeDao,
    val taskDao: TaskDao,
    val laborPlanDao: LaborPlanDao,
    val laborActualDao: LaborActualDao,
    val laborMasterDao: LaborMasterDao,
    val craftMasterDao: CraftMasterDao
) : Worker(context, workerParameters) {
    private val TAG = LaborWorker::class.java.name

    var workOrderRepository: WorkOrderRepository
    var response = WorkOrderResponse()
    var laborRepository: LaborRepository
    var taskRepository: TaskRepository

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
                materialBackupDao, matusetransDao,
                wpmaterialDao, materialDao, worklogDao,
                worklogTypeDao,
                taskDao, laborPlanDao, laborActualDao, laborMasterDao, craftMasterDao
            )
        workOrderRepository = workerRepository.buildWorkorderRepository()
        laborRepository = workerRepository.buildLaborRepository()
        taskRepository = workerRepository.buildTaskRepository()

        Timber.tag(TAG).i("LaborWorker() workOrderRepository: %s", workOrderRepository)
    }

    private val MAX_RUN_ATTEMPT = 6
    override fun doWork(): Result {
        return try {
            Timber.tag(TAG).d("doWork() LaborWorker")
            if (runAttemptCount > MAX_RUN_ATTEMPT) {
                Result.failure()
            }

            syncUpdateLabor()
            Result.success()

        } catch (e: Exception) {
            Timber.tag(TAG).e("doWork() error: %s", e.message)
            Result.retry()
        }
    }


    fun syncUpdateLabor() {
        val tempCacheWithId = mutableListOf<LaborPlanEntity>()
        val tempCacheWithoutId = mutableListOf<LaborPlanEntity>()
        val index = 0
        //Query to local
        val listLaborPlanLocal = laborRepository.findListLaborPlanBySyncUpdateAndLocally(
            BaseParam.APP_FALSE,
            BaseParam.APP_TRUE
        )

        listLaborPlanLocal.whatIfNotNullOrEmpty { caches ->
            caches.forEach { laborplan ->
                laborplan.wplaborid.whatIfNotNull(
                    whatIf = {
                        tempCacheWithId.add(laborplan)
                    },
                    whatIfNot = {
                        tempCacheWithoutId.add(laborplan)
                    }
                )
            }
        }

        tempCacheWithId.whatIfNotNullOrEmpty {
            updateLaborPlan(it, index)
        }

        tempCacheWithoutId.whatIfNotNullOrEmpty {
            updateCreateLaborPlan(it, index)
        }

        Timber.tag(TAG)
            .d("LaborWorker() syncUpdateLabor() tempCacheWithId size %s", tempCacheWithId.size)
        Timber.tag(TAG).d(
            "LaborWorker() syncUpdateLabor() tempCacheWithoutId size %s",
            tempCacheWithoutId.size
        )
    }


    /**
     * Update Labor Plan
     */
    private fun updateLaborPlan(listLaborPlan: List<LaborPlanEntity>, currentIndex: Int) {
        //TODO Http request to Update Labor plan
        val laborPlanEntity = listLaborPlan[currentIndex]
        laborPlanEntity.whatIfNotNull {
            val member = prepareBodyUpdateTomaximoOfflineMode(it)
            val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
            val xMethodeOverride: String = BaseParam.APP_PATCH
            val contentType: String = ("application/json")
            val patchType: String = BaseParam.APP_MERGE
            member.whatIfNotNull { member ->
                runBlocking {
                    launch(Dispatchers.IO) {
                        it.workorderid.whatIfNotNull { workorderid ->
                            workOrderRepository.updateLaborPlan(cookie,
                                xMethodeOverride,
                                contentType,
                                patchType,
                                workorderid.toInt(),
                                member,
                                onSuccess = {
                                    Timber.tag(TAG).i("updateLaborPlanToMaximo() update local cache after update")
                                    handlingUpdateLaborPlan(laborPlanEntity, BaseParam.APP_TRUE, BaseParam.APP_TRUE)
                                },
                                onError = {
                                    Timber.tag(TAG).i("updateLaborPlanToMaximo() onError() onError: %s", it)
                                    handlingUpdateLaborPlan(laborPlanEntity, BaseParam.APP_FALSE, BaseParam.APP_TRUE)

                                }
                            )
                        }
                    }
                }
            }
        }
    }

    fun prepareBodyUpdateTomaximoOfflineMode(laborPlanEntity: LaborPlanEntity): Member {
        //need prepare body, and validation with task or without task
        val wplaborid = laborPlanEntity.wplaborid.toString()
        val taskid = laborPlanEntity.taskid.toString()
        val wonumTask = laborPlanEntity.wonumTask.toString()
        val laborCode = laborPlanEntity.laborcode.toString()
        val craft = laborPlanEntity.craft.toString()
        val prepareBody = Member()

        if (laborPlanEntity.isTask == BaseParam.APP_TRUE) {
            //Prepare body with task
            val bodyLaborplanWithTask =
                laborRepository.preapreBodyLaborPlanTask(wplaborid, taskid, wonumTask)
            bodyLaborplanWithTask.whatIfNotNullOrEmpty {
                prepareBody.woactivity = it
            }
        } else {
            //Prepare body without task
            val bodyLaborplanWithoutTask =
                laborRepository.preapreBodyLaborPlanNontask(wplaborid, laborCode, craft)
            bodyLaborplanWithoutTask.whatIfNotNullOrEmpty {
                prepareBody.wplabor = it
            }
        }
        return prepareBody
    }

    fun handlingUpdateLaborPlan(laborPlanEntity: LaborPlanEntity, syncUpdate: Int, isLocally: Int) {
        laborPlanEntity.syncUpdate = syncUpdate
        laborPlanEntity.isLocally = isLocally
        laborRepository.saveLaborPlanCache(laborPlanEntity)
    }

    /**
     * Update Create Labor plan
     */
    private fun updateCreateLaborPlan(listLaborPlan: List<LaborPlanEntity>, currentIndex: Int) {
        //TODO Http request to create labor plan
        val laborPlanEntity = listLaborPlan[currentIndex]
        laborPlanEntity.whatIfNotNull {
            Timber.tag(TAG).d("LaborWorker() updateCreateLaborPlan() laborcode %s", it.laborcode)
            val member = prepareBodyCreateLaborPlanOfflinemode(it)
            val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
            val xMethodeOverride: String = BaseParam.APP_PATCH
            val contentType: String = ("application/json")
            val patchType: String = BaseParam.APP_MERGE
            val properties: String = BaseParam.APP_ALL_PROPERTIES

            member.whatIfNotNull { member ->
                runBlocking {
                    launch(Dispatchers.IO) {
                        it.workorderid.whatIfNotNull { workorderid ->
                            workOrderRepository.createLaborPlan(cookie,
                                xMethodeOverride,
                                contentType,
                                patchType,
                                properties,
                                workorderid.toInt(),
                                member,
                                onSuccess = { response ->
                                    Timber.tag(TAG).i(
                                        "LaborWorker() updateCreateLaborPlan() onSuccess() %s",
                                        response
                                    )
                                    response.whatIfNotNull { member ->
                                        handlingCreateLaborPlan(member, laborPlanEntity)
                                        val nextIndex = currentIndex + 1
                                        if (nextIndex <= listLaborPlan.size - 1) {
                                            updateCreateLaborPlan(listLaborPlan, nextIndex)
                                        }
                                    }
                                },
                                onError = {
                                    Timber.tag(TAG)
                                        .i("LaborWorker() updateCreateLaborPlan() onError() onError: %s", it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun prepareBodyCreateLaborPlanOfflinemode(laborPlanEntity: LaborPlanEntity): Member {
        val member = Member()
        if(laborPlanEntity.isTask == BaseParam.APP_TRUE) {
            val taskList =
                taskRepository.prepareBodyForCreateLaborPlanWithTaskOfflinemode(laborPlanEntity)
            taskList.whatIfNotNullOrEmpty { tasks ->
                member.woactivity = tasks
            }
        } else {
            val laborplanList = laborRepository.prepareBodyLaborPlanOfflinemode(laborPlanEntity)


            laborplanList.whatIfNotNullOrEmpty { wplabors ->
                member.wplabor = wplabors
            }
        }
        return member
    }

    private fun handlingCreateLaborPlan(member: Member, laborPlanEntity: LaborPlanEntity) {
        val isTask = laborPlanEntity.isTask
        val listLpTask = member.woactivity
        val listLpNonTask = member.wplabor

        if (isTask == BaseParam.APP_TRUE) {
            listLpTask.whatIfNotNullOrEmpty { list ->
                list.forEach {
                    it.wplabor.whatIfNotNullOrEmpty { wplabors ->
                        wplabors.forEach { labor ->
                            val wplaborid = labor.wplaborid
                            laborPlanEntity.wonumTask = it.wonum
                            checkingLaborPlanExisting(wplaborid.toString(), laborPlanEntity)
                        }
                    }
                }
            }
        } else {
            listLpNonTask.whatIfNotNullOrEmpty {
                it.forEach { wplabor ->
                    val wplaborid = wplabor.wplaborid
                    checkingLaborPlanExisting(wplaborid.toString(), laborPlanEntity)
                }
            }
        }
    }

    private fun checkingLaborPlanExisting(wplaborid: String, laborPlanEntity: LaborPlanEntity) {
        val cacheLaborPlan = laborRepository.findLaborPlanByWplaborid(wplaborid)
        if (cacheLaborPlan == null) {
            Timber.tag(TAG)
                .i("LaborWorker() checkingLaborPlanExisting() update local cache after update")
            laborPlanEntity.wplaborid = wplaborid
            laborPlanEntity.syncUpdate = BaseParam.APP_TRUE
            laborPlanEntity.isLocally = BaseParam.APP_TRUE
            laborRepository.saveLaborPlanCache(laborPlanEntity)
        }
    }

}