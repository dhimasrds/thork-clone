package id.thork.app.workmanager

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.response.task_response.TaskResponse
import id.thork.app.persistence.dao.TaskDao
import id.thork.app.persistence.entity.TaskEntity
import id.thork.app.repository.TaskRepository
import id.thork.app.repository.WorkerTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

/**
 * Created by Raka Putra on 7/6/21
 * Jakarta, Indonesia.
 */
class TaskWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    val appSession: AppSession,
    val preferenceManager: PreferenceManager,
    val httpLoggingInterceptor: HttpLoggingInterceptor,
    val taskDao: TaskDao,
) : Worker(context, workerParameters) {

    private val TAG = TaskWorker::class.java.name

    var taskRepository: TaskRepository

    init {
        val workTaskRepository = WorkerTaskRepository(
            appSession, taskDao, httpLoggingInterceptor, preferenceManager

        )
        taskRepository = workTaskRepository.buildTaskRepository()
    }

    private val MAX_RUN_ATTEMPT = 6

    override fun doWork(): Result {
        try {
            //Query Local WO Record is needed to sync with the server
            if (runAttemptCount > MAX_RUN_ATTEMPT) {
                Result.failure()
            }
            syncTask()
            return Result.success()

        } catch (e: Exception) {
            Timber.tag(TAG).e("doWork() error: %s", e.message)
            return Result.retry()
        }
    }

    private fun syncTask() {
        val listTask = taskRepository.findTaskListByOfflineModeAndIsFromWoDetail(BaseParam.APP_TRUE, BaseParam.APP_TRUE)
        Timber.d("syncTask() listSize %s ", listTask.size)
        val index = 0
        listTask.whatIfNotNullOrEmpty {
            updateTaskToMaximoInOfflineMode(it, index)
        }
    }

    private fun updateTaskToMaximoInOfflineMode(listTask: List<TaskEntity>, currentIndex: Int) {

        val currentList = listTask[currentIndex]
        val woid = listTask[currentIndex].woId

        val xmethodeOverride: String = BaseParam.APP_PATCH
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val contentType: String = ("application/json")
        val patchType: String = BaseParam.APP_MERGE
        val properties = BaseParam.APP_ALL_PROPERTIES

        woid.whatIfNotNull { woId ->
            val member = taskRepository.prepareTaskBodyInOfflineMode(currentList)
            val taskResponse = TaskResponse()
            member.whatIfNotNullOrEmpty {
                taskResponse.woactivity = it
            }

            runBlocking {
                launch(Dispatchers.IO) {
                        taskRepository.createTaskToMx(
                            xmethodeOverride,
                            contentType,
                            cookie,
                            patchType,
                            properties,
                            woId,
                            taskResponse,
                            onSuccess = { woMember ->
                                    woMember.woactivity.whatIfNotNullOrEmpty {
                                        Timber.tag(TAG).i(
                                            "updateTaskToMaximoInOfflineMode() onSuccess() onSuccess: %s",
                                            it
                                        )
                                        taskRepository.handlingTaskSuccesInOfflineMode(
                                            it,
                                            currentList,
                                            woId
                                        )
                                }
                                val nextIndex = currentIndex + 1
                                if (nextIndex <= listTask.size - 1) {
                                    Timber.d("updateTaskToMaximoInOfflineMode() nextIndex %s ", nextIndex)
                                    updateTaskToMaximoInOfflineMode(listTask, nextIndex)
                                }
                            },
                            onError = {
                                Timber.tag(TAG).i(
                                    "updateTaskToMaximoInOfflineMode() onError() onError: %s", it
                                )
                            }
                        )
                }
            }
        }
    }

}