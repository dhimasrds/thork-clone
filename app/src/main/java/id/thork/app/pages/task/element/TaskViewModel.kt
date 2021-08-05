package id.thork.app.pages.task.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.response.task_response.TaskResponse
import id.thork.app.persistence.entity.TaskEntity
import id.thork.app.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Raka Putra on 6/23/21
 * Jakarta, Indonesia.
 */
class TaskViewModel @ViewModelInject constructor(
    private val appSession: AppSession,
    private val taskRepository: TaskRepository,
    private val preferenceManager: PreferenceManager,
) : LiveCoroutinesViewModel() {
    val TAG = TaskViewModel::class.java.name

    private val _listTask = MutableLiveData<List<TaskEntity>>()
    private val _listTaskCreateWo = MutableLiveData<List<TaskEntity>>()

    val listTask: LiveData<List<TaskEntity>> get() = _listTask
    val listTaskCreateWo: LiveData<List<TaskEntity>> get() = _listTaskCreateWo

    fun initTask(workoderid: Int) {
        val task = taskRepository.findListTaskByWoid(workoderid)
        task.whatIfNotNullOrEmpty {
            _listTask.value = it
        }
    }

    fun initTaskCreateWo(wonum: String) {
        val task = taskRepository.findListTaskByWonum(wonum)
        task.whatIfNotNullOrEmpty {
            _listTaskCreateWo.value = it
        }
    }

    fun estDuration(jam: Int, menit: Int): Double {
        val hasil: Double
        val hasilDetik: Int = jam * 3600 + menit * 60
        val hasilDetikDouble = hasilDetik.toDouble()
        hasil = hasilDetikDouble / 3600
        return hasil
    }

    fun saveCacheFromCreateWo(
        woid: Int?, wonum: String?, taskId: Int?,
        desc: String?, scheduleStart: String?, estDur: Double?, actualStart: String?,
        status: String?, syncStatus: Int?, offlineMode: Int?, isFromWoDetail: Int?
    ) {
        taskRepository.saveCache(
            woid,
            wonum,
            taskId,
            desc,
            scheduleStart,
            estDur,
            actualStart,
            status,
            syncStatus,
            offlineMode,
            isFromWoDetail
        )
    }

    fun saveCache(
        woid: Int?, wonum: String?, taskId: Int?,
        desc: String?, scheduleStart: String?, estDur: Double?, actualStart: String?,
        status: String?, syncStatus: Int?, offlineMode: Int?, isFromWoDetail: Int?
    ) {
        taskRepository.saveCache(
            woid,
            wonum,
            taskId,
            desc,
            scheduleStart,
            estDur,
            actualStart,
            status,
            syncStatus,
            offlineMode,
            isFromWoDetail
        )
        woid.whatIfNotNull {
            taskId.whatIfNotNull { taskId ->
                scheduleStart.whatIfNotNull { schedule ->
                    wonum.whatIfNotNull { wonum ->
                        updateToMaximo(it, taskId, schedule, wonum)
                    }
                }
            }
        }
    }

    fun updateTaskOnline(
        woid: Int?, taskId: Int?,
        desc: String?, scheduleStart: String?, estDur: Double?, actualStart: String?,
    ) {
        woid.whatIfNotNull {
            taskId.whatIfNotNull { taskId ->
        taskRepository.updateTaskModule(
            it,
            taskId,
            desc,
            scheduleStart,
            estDur,
            actualStart
        )
                scheduleStart.whatIfNotNull { schedule ->
                        Timber.d("raka %s ", "sukses")
                    }
            }
        }
    }

    private fun updateToMaximo(woid: Int, taskId: Int, scheduleStart: String, wonum: String) {

        val taskList = taskRepository.prepareTaskBodyFromWoDetail(woid, scheduleStart)
        val taskResponse = TaskResponse()
        taskList.whatIfNotNullOrEmpty {
            taskResponse.woactivity = it
        }
        val moshi = Moshi.Builder().build()
        val memberJsonAdapter: JsonAdapter<TaskResponse> = moshi.adapter(TaskResponse::class.java)
        Timber.tag(TAG).d("updateToMaximo() results: %s", memberJsonAdapter.toJson(taskResponse))

        val xmethodeOverride: String = BaseParam.APP_PATCH
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val contentType: String = ("application/json")
        val patchType: String = BaseParam.APP_MERGE
        val properties = BaseParam.APP_ALL_PROPERTIES
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.createTaskToMx(
                xmethodeOverride,
                contentType,
                cookie,
                patchType,
                properties,
                woid,
                taskResponse,
                onSuccess = { woMember ->
                    woMember.woactivity.whatIfNotNullOrEmpty {
                        Timber.tag(TAG).i("updateToMaximo() onSuccess() onSuccess: %s", it)
                        taskRepository.handlingTaskSuccesFromWoDetail(it, woid, wonum)
                    }
                },
                onError = {
                    taskRepository.handlingTaskFailedFromWoDetail(woid, taskId)
                    Timber.tag(TAG).i("updateToMaximo() onError() onError: %s", it)

                }
            )
        }

    }
}
