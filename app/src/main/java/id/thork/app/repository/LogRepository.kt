package id.thork.app.repository

import id.thork.app.base.BaseRepository
import id.thork.app.persistence.dao.LogDao
import id.thork.app.persistence.entity.LogEntity

/**
 * Created by Raka Putra on 2/25/21
 * Jakarta, Indonesia.
 */
class LogRepository constructor(private val logDao: LogDao) : BaseRepository() {
    fun getAllLog(): List<LogEntity> {
        return logDao.findLogs()
    }

    fun pagingLog(position: Long, limit: Long): List<LogEntity>? {
        return logDao.pagingLog(position, limit)
    }

    fun getLogById(id: Long): LogEntity? {
        return logDao.getLogById(id)
    }
}