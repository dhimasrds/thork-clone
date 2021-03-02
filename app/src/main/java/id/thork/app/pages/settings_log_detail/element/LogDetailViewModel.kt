package id.thork.app.pages.settings_log_detail.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.dao.LogDao
import id.thork.app.persistence.dao.LogDaoImp
import id.thork.app.persistence.entity.LogEntity
import id.thork.app.repository.LogRepository

/**
 * Created by Raka Putra on 1/19/21
 * Jakarta, Indonesia.
 */
class LogDetailViewModel @ViewModelInject constructor():LiveCoroutinesViewModel() {

    private val logDao: LogDao
    private val repository: LogRepository

    init {
        logDao = LogDaoImp()
        repository = LogRepository(logDao)
    }

    fun getLogById(id: Long): LogEntity? {
        return repository.getLogById(id)
    }
}