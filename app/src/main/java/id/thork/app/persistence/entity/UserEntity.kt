package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity
import java.util.*

@Entity
class UserEntity : BaseEntity {
    var username: String? = null
    var personUID: Int? = null
    var userid: String? = null
    var pattern: String? = null
    var session = 0
    var isPattern = 0
    var lastLogin: Date? = null
    var orgid: String? = null
    var siteid: String? = null
    var organization: String? = null
    var site: String? = null
    var language: String? = null
    var privileges: String? = null
    var userHash: String? = null
    var longitude: String? = null
    var latitude: String? = null
    var server_address: String? = null
    var laborcode: String? = null
    var apiKey: String? = null

    constructor()
}