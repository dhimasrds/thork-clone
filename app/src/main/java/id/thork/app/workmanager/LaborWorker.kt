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
import id.thork.app.persistence.entity.LaborActualEntity
import id.thork.app.persistence.entity.LaborPlanEntity
import id.thork.app.repository.LaborRepository
import id.thork.app.repository.TaskRepository
import id.thork.app.repository.WorkOrderRepository
import id.thork.app.repository.WorkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    }

    private val MAX_RUN_ATTEMPT = 6
    override fun doWork(): Result {
        return try {
            Timber.tag(TAG).d("doWork() LaborWorker")
            if (runAttemptCount > MAX_RUN_ATTEMPT) {
                Result.failure()
            }

            syncUpdateLabor()
            syncUpdateLaborActual()
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
    }

    /**
     * Update Labor Plan
     */
    private fun updateLaborPlan(listLaborPlan: List<LaborPlanEntity>, currentIndex: Int) {
        //TODO Http request to Update Labor plan
        val laborPlanEntity = listLaborPlan[currentIndex]
        laborPlanEntity.whatIfNotNull {
            val member = laborRepository.prepareBodyUpdateLaborPlanTomaximo(it)
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
                                    laborRepository.handlingUpdateLaborPlan(
                                        laborPlanEntity,
                                        BaseParam.APP_TRUE,
                                        BaseParam.APP_TRUE
                                    )
                                },
                                onError = {
                                    Timber.tag(TAG)
                                        .i("updateLaborPlanToMaximo() onError() onError: %s", it)
                                    laborRepository.handlingUpdateLaborPlan(
                                        laborPlanEntity,
                                        BaseParam.APP_FALSE,
                                        BaseParam.APP_TRUE
                                    )

                                }
                            )
                        }
                    }
                }
            }
        }
    }


    /**
     * Update Create Labor plan
     */
    private fun updateCreateLaborPlan(listLaborPlan: List<LaborPlanEntity>, currentIndex: Int) {
        //TODO Http request to create labor plan
        val laborPlanEntity = listLaborPlan[currentIndex]
        laborPlanEntity.whatIfNotNull {
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
                                    response.whatIfNotNull { member ->
                                        laborRepository.handlingCreateLaborPlan(
                                            member,
                                            laborPlanEntity
                                        )
                                        val nextIndex = currentIndex + 1
                                        if (nextIndex <= listLaborPlan.size - 1) {
                                            updateCreateLaborPlan(listLaborPlan, nextIndex)
                                        }
                                    }
                                },
                                onError = {
                                    Timber.tag(TAG)
                                        .i(
                                            "LaborWorker() updateCreateLaborPlan() onError() onError: %s",
                                            it
                                        )
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
        if (laborPlanEntity.isTask == BaseParam.APP_TRUE) {
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

    /**
     * Labor Actual
     */

    fun syncUpdateLaborActual() {
        val tempCacheWithId = mutableListOf<LaborActualEntity>()
        val tempCacheWitoutId = mutableListOf<LaborActualEntity>()
        val index = 0

        val listLaborActualLocal = laborRepository.findListLaborActualBySyncUpdateAndLocally(
            BaseParam.APP_FALSE,
            BaseParam.APP_TRUE
        )

        listLaborActualLocal.whatIfNotNullOrEmpty { caches ->
            caches.forEach { laboractual ->
                laborRepository.lockingLaborActual(laboractual, true)
                laboractual.labtransid.whatIfNotNull(
                    whatIf = {
                        tempCacheWithId.add(laboractual)
                    },
                    whatIfNot = {
                        tempCacheWitoutId.add(laboractual)
                    }
                )
            }

            tempCacheWithId.whatIfNotNullOrEmpty {
                updateLaborActual(it, index)
            }

            tempCacheWitoutId.whatIfNotNullOrEmpty {
                createLaborActual(it, index)
            }
        }
    }

    /**
     * Create Labor Actual
     */

    fun createLaborActual(listLaborActual: List<LaborActualEntity>, currentIndex: Int) {
        val laboractualEntity = listLaborActual[currentIndex]
        val workorderid = laboractualEntity.workorderid
        laborRepository.lockingLaborActual(laboractualEntity, true)
        val member = prepareBodyCreateLaborActualOfflinemode(laboractualEntity)
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType: String = ("application/json")
        val patchType: String = BaseParam.APP_MERGE
        val properties: String = BaseParam.APP_ALL_PROPERTIES

        member.whatIfNotNull { body ->
            runBlocking {
                launch(Dispatchers.IO) {
                    workorderid.whatIfNotNull { woid ->
                        workOrderRepository.createLaborActual(
                            cookie,
                            xMethodeOverride,
                            contentType,
                            patchType,
                            properties,
                            woid.toInt(),
                            body,
                            onSuccess = { response ->
                                response.whatIfNotNull {
                                    laborRepository.handlingCreateLaborActual(it, laboractualEntity)
                                    laborRepository.lockingLaborActual(laboractualEntity, false)

                                    val nextIndex = currentIndex + 1
                                    if (nextIndex <= listLaborActual.size - 1) {
                                        createLaborActual(listLaborActual, nextIndex)
                                    }
                                }
                            },
                            onError = {
                                laborRepository.lockingLaborActual(laboractualEntity, false)
                                Timber.tag(TAG).i("createLaborActual() onError() onError: %s", it)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun prepareBodyCreateLaborActualOfflinemode(laborActualEntity: LaborActualEntity): Member {
        val member = Member()
        laborActualEntity.workorderid.whatIfNotNull {
            if (laborActualEntity.isTask == BaseParam.APP_TRUE) {
                val tasklist = taskRepository.prepareBodyForCreateLaborActualWithTask(it.toInt())
                tasklist.whatIfNotNullOrEmpty { task ->
                    member.woactivity = task
                }
            } else {
                val laboractualList =
                    laborRepository.prepareBodyLaborActualNonTaskOfflineMode(laborActualEntity)
                laboractualList.whatIfNotNullOrEmpty { labtrans ->
                    member.labtrans = labtrans
                }
            }
        }
        Timber.tag(TAG).d("laborworker prepareBodyCreateLaborActualOfflinemode() :%s", member)
        return member
    }

    /**
     * update labor actual
     */

    fun updateLaborActual(listLaborActual: List<LaborActualEntity>, currentIndex: Int) {
        val laborActualEntity = listLaborActual[currentIndex]
        val labtranid = laborActualEntity.labtransid
        laborRepository.lockingLaborActual(laborActualEntity, true)
        val labtran = laborRepository.prepareBodyUpdateLaborActual(laborActualEntity)
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType: String = ("application/json")
        val patchType: String = BaseParam.APP_MERGE

        labtran.whatIfNotNull { labtran ->
            runBlocking {
                launch(Dispatchers.IO) {
                    labtranid.whatIfNotNull { labtranid ->
                        workOrderRepository.updateLaborActual(
                            cookie,
                            xMethodeOverride,
                            contentType,
                            patchType,
                            labtranid.toInt(),
                            labtran,
                            onSuccess = {
                                Timber.tag(TAG)
                                    .i("updateLaborActual() update local cache after update")
                                laborRepository.handlingUpdateLaborActual(
                                    laborActualEntity,
                                    BaseParam.APP_TRUE
                                )
                                laborRepository.lockingLaborActual(laborActualEntity, false)

                                val nextIndex = currentIndex + 1
                                if (nextIndex <= listLaborActual.size - 1) {
                                    updateLaborActual(listLaborActual, nextIndex)
                                }
                            },
                            onError = {
                                Timber.tag(TAG).i("updateLaborActual() onError() onError: %s", it)
                            }
                        )
                    }
                }
            }
        }
    }
}