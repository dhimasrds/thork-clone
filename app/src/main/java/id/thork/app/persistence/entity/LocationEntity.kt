package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity


@Entity
class LocationEntity : BaseEntity {
    var location: String? = null
    var description: String? = null
    var status: String? = null
    var formatAddress: String? = null
    var thisfsmrfid: String? = null
    var thisfsmtagtime: String? = null
    var thisfsmtagprogress: String? = null
    var image: String? = null
    var latitudey: Double? = null
    var longitudex: Double? = null

    constructor()
}

