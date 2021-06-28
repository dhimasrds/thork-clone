package id.thork.app.pages.task.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.persistence.entity.TaskEntity
import id.thork.app.repository.TaskRepository

/**
 * Created by Raka Putra on 6/23/21
 * Jakarta, Indonesia.
 */
class TaskViewModel @ViewModelInject constructor(
    private val appSession: AppSession,
    private val taskRepository: TaskRepository,
) : LiveCoroutinesViewModel() {
    val TAG = TaskViewModel::class.java.name

    private val _listTask = MutableLiveData<List<TaskEntity>>()

    val listTask: LiveData<List<TaskEntity>> get() = _listTask

    fun initTask(workoderid: Int) {
        val task = taskRepository.findListTaskByWoid(workoderid)
        task.whatIfNotNullOrEmpty {
            _listTask.value = it
        }
    }

    fun estDuration(jam: Int, menit: Int): Double {
        val hasil: Double
        val hasilDetik: Int = jam * 3600 + menit * 60
        val hasilDetikDouble = hasilDetik.toDouble()
        hasil = hasilDetikDouble / 3600
        return hasil
    }

    fun saveCache(
        woid: Int?, wonum: String?, taskId: Int?,
        desc: String?, scheduleStart: String?, estDur: Double?, actualStart: String?,
        status: String?,
    ) {
        taskRepository.saveCache(woid,
            wonum,
            taskId,
            desc,
            scheduleStart,
            estDur,
            actualStart,
            status)
    }
}