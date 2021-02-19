package id.thork.app.pages.settings_log.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.dao.LogDao
import id.thork.app.persistence.dao.LogDaoImp
import id.thork.app.persistence.entity.LogEntity

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class LogViewModel @ViewModelInject constructor() : LiveCoroutinesViewModel() {
    private val logDao: LogDao

    init {
        logDao = LogDaoImp()
    }

    fun findLogs(): List<LogEntity> {
        return logDao.findLogs()
    }
}