package id.thork.app.pages.settings_log.element

import androidx.paging.PagingSource
import id.thork.app.persistence.dao.LogDao
import id.thork.app.persistence.dao.LogDaoImp
import id.thork.app.persistence.entity.LogEntity
import id.thork.app.repository.LogRepository
import kotlinx.coroutines.delay
import java.io.IOException

/**
 * Created by Raka Putra on 2/25/21
 * Jakarta, Indonesia.
 */
private const val PAGE_INDEX = 1

class LogPagingSource(private var logDao: LogDao,
                      private var repository: LogRepository
) : PagingSource<Int, LogEntity>() {

    init {
        logDao = LogDaoImp()
        repository = LogRepository(logDao)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LogEntity> {
        val position = params.key ?: PAGE_INDEX
        return try {
            val listLog = mutableListOf<LogEntity>()
            val list = repository.pagingLog(position.toLong(), 3)
            list?.let { listLog.addAll(it) }
            delay(3000)
            LoadResult.Page(
                data = listLog,
                prevKey = if (position == PAGE_INDEX) null else position,
                nextKey = if (listLog.isEmpty()) null else position + 3
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        }
    }
}