package id.thork.app.pages.profiles.setting.settings_log.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.dao.LogDao
import id.thork.app.persistence.dao.LogDaoImp
import id.thork.app.persistence.entity.LogEntity
import id.thork.app.repository.LogRepository

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class LogViewModel @ViewModelInject constructor() : LiveCoroutinesViewModel() {
    private val logDao: LogDao
    private val repository: LogRepository
    val getAllLogs: List<LogEntity>

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }

    init {
        logDao = LogDaoImp()
        repository = LogRepository(logDao)
        getAllLogs = repository.getAllLog()
    }

    val logList = Pager(
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = true
        ),
        pagingSourceFactory = { LogPagingSource(logDao, repository) }
    ).flow.cachedIn(viewModelScope)
}