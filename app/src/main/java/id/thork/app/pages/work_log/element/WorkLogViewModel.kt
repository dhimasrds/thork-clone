package id.thork.app.pages.work_log.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.Worklog
import id.thork.app.persistence.entity.WorklogEntity
import id.thork.app.persistence.entity.WorklogTypeEntity
import id.thork.app.repository.WorkOrderRepository
import id.thork.app.repository.WorklogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 07/06/21
 * Jakarta, Indonesia.
 */
class WorkLogViewModel @ViewModelInject constructor(
    private val worklogRepository: WorklogRepository,
    private val workOrderRepository: WorkOrderRepository,
    private val preferenceManager: PreferenceManager,

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

    fun saveWorklog(
        summary: String,
        description: String,
        type: String,
        wonum: String,
        workorderid: String
    ) {
        worklogRepository.saveWorklogEntity(summary, description, type, wonum, workorderid)

        //Prepare Member to update worklog
        val listWorklog = mutableListOf<Worklog>()
        val worklog = worklogRepository.prepareBodyWorklog(summary, description, type, wonum)
        listWorklog.add(worklog)

        val member = Member()
        listWorklog.whatIfNotNullOrEmpty {
            member.worklog = it
        }

        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType: String = ("application/json")
        val patchType: String = BaseParam.APP_MERGE

        viewModelScope.launch(Dispatchers.IO) {
            workOrderRepository.updateStatus(cookie,
                xMethodeOverride,
                contentType,
                patchType,
                workorderid.toInt(),
                member,
                onSuccess = {
                    worklogRepository.handlingAfterUpdate(workorderid)
                },
                onError = {
                    Timber.tag(TAG).i("updateWorklog() onError() onError: %s", it)
                })
        }


        _result.value = BaseParam.APP_TRUE
    }


}