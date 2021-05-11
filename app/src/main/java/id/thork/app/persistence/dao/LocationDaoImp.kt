package id.thork.app.persistence.dao

import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.LocationEntity
import id.thork.app.persistence.entity.LocationEntity_
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.persistence.entity.WoCacheEntity_

class LocationDaoImp : LocationDao {
    private var locationEntityBox = ObjectBox.boxStore.boxFor(LocationEntity::class.java)

    override fun saveLocation(locationEntity: LocationEntity):LocationEntity {
        locationEntityBox.put(locationEntity)
        return locationEntity    }

    override fun deleteLocation() {
        locationEntityBox.removeAll()
    }

    override fun locationList(): List<LocationEntity> {
        return locationEntityBox.query().notNull(LocationEntity_.location).build()
            .find()
    }
}