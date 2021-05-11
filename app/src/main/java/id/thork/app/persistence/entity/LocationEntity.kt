package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity


@Entity
class LocationEntity : BaseEntity{
    var location :String? = null
    var description :String? = null
    var status :String? = null
    var formatAddress :String? = null
    var latitudey :Double? = null
    var latitudex :Double? = null

    constructor()
}

