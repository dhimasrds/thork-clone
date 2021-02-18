package id.thork.app.pages.settings_language.element

import androidx.paging.PagingSource
import id.thork.app.persistence.dao.LanguageDao
import id.thork.app.persistence.dao.LanguageDaoImp
import id.thork.app.persistence.entity.LanguageEntity
import id.thork.app.repository.LanguageRepository
import kotlinx.coroutines.delay
import java.io.IOException

/**
 * Created by Raka Putra on 2/17/21
 * Jakarta, Indonesia.
 */

private const val PAGE_INDEX = 1


class SettingsLanguagePagingSource(
    private var dao: LanguageDao,
    private var repository: LanguageRepository
) : PagingSource<Int, LanguageEntity>() {

    init {
        dao = LanguageDaoImp()
        repository = LanguageRepository(dao)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LanguageEntity> {
        val position = params.key ?: PAGE_INDEX
        return try {
            val listNotes = mutableListOf<LanguageEntity>()
            val list = repository.pagingLanguage(position.toLong(), 3)
            list?.let { listNotes.addAll(it) }
            delay(100)
            LoadResult.Page(
                data = listNotes,
                prevKey = if (position == PAGE_INDEX) null else position,
                nextKey = if (listNotes.isEmpty()) null else position + 3
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        }
    }
}