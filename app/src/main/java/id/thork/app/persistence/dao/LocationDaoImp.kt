package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.LocationEntity
import id.thork.app.persistence.entity.LocationEntity_

class LocationDaoImp : LocationDao {
    private var locationEntityBox = ObjectBox.boxStore.boxFor(LocationEntity::class.java)

    override fun saveLocation(locationEntity: LocationEntity): LocationEntity {
        locationEntityBox.put(locationEntity)
        return locationEntity
    }

    override fun deleteLocation() {
        locationEntityBox.removeAll()
    }

    override fun locationList(): List<LocationEntity> {
        return locationEntityBox.query().notNull(LocationEntity_.location).build()
            .find()
    }

    override fun findByLocation(location: String): LocationEntity? {
        val locationEntity =
            locationEntityBox.query().equal(LocationEntity_.location, location).build().find()
        locationEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findByTagcode(tagcode: String): LocationEntity? {
        val locationEntity =
            locationEntityBox.query().equal(LocationEntity_.thisfsmrfid, tagcode).build().find()
        locationEntity.whatIfNotNull {
            return it[0]
        }
        return null
    }
}