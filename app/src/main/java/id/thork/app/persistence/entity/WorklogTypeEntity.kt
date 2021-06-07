package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity

/**
 * Created by M.Reza Sulaiman on 07/06/2021
 * Jakarta, Indonesia.
 */
@Entity
class WorklogTypeEntity : BaseEntity() {
    var type: String? = null
    var description: String? = null
}