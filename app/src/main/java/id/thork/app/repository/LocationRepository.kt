package id.thork.app.repository

import id.thork.app.persistence.dao.LocationDao
import id.thork.app.persistence.dao.LocationDaoImp
import id.thork.app.persistence.entity.LocationEntity
import javax.inject.Inject

/**
 * Created by M.Reza Sulaiman on 19/05/2021
 * Jakarta, Indonesia.
 */
class LocationRepository @Inject constructor(
    private val locationDao: LocationDao) {

    val TAG = AssetRepository::class.java.name

    fun findByLocation(location: String) : LocationEntity? {
        return locationDao.findByLocation(location)
    }

    fun findByTagcode(tagcode: String) : LocationEntity? {
        return locationDao.findByTagcode(tagcode)
    }

}