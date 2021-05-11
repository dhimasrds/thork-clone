package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.LocationEntity
import id.thork.app.persistence.entity.UserEntity

interface LocationDao {
    fun saveLocation(locationEntity: LocationEntity) : LocationEntity
    fun deleteLocation()
    fun locationList():List<LocationEntity>
}