package id.thork.app.persistence.dao

import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.LanguageEntity
import id.thork.app.persistence.entity.LanguageEntity_
import io.objectbox.Box

/**
 * Created by Raka Putra on 2/17/21
 * Jakarta, Indonesia.
 */
class LanguageDaoImp: LanguageDao {
    var languageEntityBox: Box<LanguageEntity>

    init {
        languageEntityBox = ObjectBox.boxStore.boxFor(LanguageEntity::class.java)
    }

    override fun getAllLanguage(): List<LanguageEntity> {
        languageEntityBox = ObjectBox.boxStore.boxFor(LanguageEntity::class.java)
        return languageEntityBox.query().notNull(LanguageEntity_.language).build().find()
    }

    override fun save(languageEntity: LanguageEntity) {
        languageEntityBox.put(languageEntity)
    }

    override fun pagingLanguage(position: Long ,limit: Long): List<LanguageEntity>? {
        languageEntityBox = ObjectBox.boxStore.boxFor(LanguageEntity::class.java)
        val languageEntities: List<LanguageEntity> =
            languageEntityBox.query().notNull(LanguageEntity_.id).build().find(position, limit)
        if (languageEntities != null){
            return languageEntities
        }
        return null
    }
}