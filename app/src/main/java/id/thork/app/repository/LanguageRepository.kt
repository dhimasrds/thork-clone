package id.thork.app.repository

import id.thork.app.base.BaseRepository
import id.thork.app.persistence.dao.LanguageDao
import id.thork.app.persistence.entity.LanguageEntity

/**
 * Created by Raka Putra on 2/17/21
 * Jakarta, Indonesia.
 */
class LanguageRepository constructor(private val languageDao: LanguageDao): BaseRepository {
    fun getAllLanguage(): List<LanguageEntity> {
        return languageDao.getAllLanguage()
    }

    fun createNote(languageEntity: LanguageEntity) {
        return languageDao.save(languageEntity)
    }

    fun pagingLanguage(position: Long ,limit: Long): List<LanguageEntity>? {
        return languageDao.pagingLanguage(position, limit)
    }
}