package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.LanguageEntity

/**
 * Created by Raka Putra on 2/17/21
 * Jakarta, Indonesia.
 */
interface LanguageDao {
    fun getAllLanguage(): List<LanguageEntity>

    fun save(languageEntity: LanguageEntity)

    fun pagingLanguage(position: Long ,limit: Long): List<LanguageEntity>?
}