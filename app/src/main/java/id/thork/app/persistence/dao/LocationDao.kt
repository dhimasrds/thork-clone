package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.LocationEntity

interface LocationDao {
    fun saveLocation(locationEntity: LocationEntity) : LocationEntity
    fun deleteLocation()
    fun locationList():List<LocationEntity>
}