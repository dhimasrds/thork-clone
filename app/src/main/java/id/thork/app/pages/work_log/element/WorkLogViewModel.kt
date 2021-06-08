package id.thork.app.pages.work_log.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.WorklogEntity
import id.thork.app.persistence.entity.WorklogTypeEntity
import id.thork.app.repository.WorklogRepository
import id.thork.app.utils.DateUtils

/**
 * Created by Dhimas Saputra on 07/06/21
 * Jakarta, Indonesia.
 */
class WorkLogViewModel @ViewModelInject constructor(
    private val worklogRepository: WorklogRepository
) : LiveCoroutinesViewModel() {
    val TAG = WorkLogViewModel::class.java.name

    private val _listWorklogType = MutableLiveData<List<WorklogTypeEntity>>()
    private val _listWorklog = MutableLiveData<List<WorklogEntity>>()
    private val _result = MutableLiveData<Int>()

    val listWorklogType: LiveData<List<WorklogTypeEntity>> get() = _listWorklogType
    val listWorklog: LiveData<List<WorklogEntity>> get() = _listWorklog
    val result: LiveData<Int> get() = _result

    fun initWorklogType() {
        val type = worklogRepository.fetchListWorklogtype()
        type.whatIfNotNullOrEmpty {
            _listWorklogType.value = it
        }
    }

    fun initWorklog(workoderid: String) {
        val worklog = worklogRepository.fetchListWorklogByWoid(workoderid)
        worklog.whatIfNotNullOrEmpty {
            _listWorklog.value = it
        }
    }

    fun saveWorklog(summary: String, description: String, type: String, wonum: String, workorderid: String) {
        worklogRepository.saveWorklogEntity(summary, description, type, wonum, workorderid)
        _result.value = BaseParam.APP_TRUE
    }




}