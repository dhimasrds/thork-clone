package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity

/**
 * Created by M.Reza Sulaiman on 30/06/2021
 * Jakarta, Indonesia.
 */
@Entity
class CraftMasterEntity :  BaseEntity() {
    var craft: String? = null
    var skillLevel: String? = null
    var laborcode: String? = null
    var name: String? = null
}