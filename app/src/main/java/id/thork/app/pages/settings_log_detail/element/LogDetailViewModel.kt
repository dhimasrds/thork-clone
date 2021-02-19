package id.thork.app.pages.settings_log_detail.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.dao.LogDao
import id.thork.app.persistence.dao.LogDaoImp
import id.thork.app.persistence.entity.LogEntity

/**
 * Created by Raka Putra on 1/19/21
 * Jakarta, Indonesia.
 */
class LogDetailViewModel @ViewModelInject constructor():LiveCoroutinesViewModel() {

    private val logDao: LogDao

    init {
        logDao = LogDaoImp()
    }

    fun findLogs(id: Long): LogEntity {
        return logDao.findLog(id)
    }
}