package id.thork.app.persistence.entity

/**
 * Created by Raka Putra on 2/19/21
 * Jakarta, Indonesia.
 */
class Language {
    var id: Long = 0
    var languageCode: String? = null
    var language: String? = null
    constructor()
    constructor(languageCode: String?, language: String?){
        this.languageCode = languageCode
        this.language = language
    }
}