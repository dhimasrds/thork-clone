package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by Raka Putra on 2/15/21
 * Jakarta, Indonesia.
 */
@Entity
class LanguageEntity {
    @Id
    var id: Long = 0
    var languageCode: String? = null
    var language: String? = null
    constructor()
    constructor(languageCode: String?, language: String?){
        this.languageCode = languageCode
        this.language = language
    }
}