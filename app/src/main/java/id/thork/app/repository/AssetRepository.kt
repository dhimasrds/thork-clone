package id.thork.app.repository

import id.thork.app.base.BaseRepository
import id.thork.app.persistence.dao.AssetDao
import id.thork.app.persistence.dao.LocationDao
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.LocationEntity
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by M.Reza Sulaiman on 18/05/2021
 * Jakarta, Indonesia.
 */
class AssetRepository @Inject constructor(
    private val assetDao: AssetDao,
    private val locationDao: LocationDao
) : BaseRepository() {
    val TAG = AssetRepository::class.java.name

    fun findbyAssetnum(assetnum : String): AssetEntity? {
        return assetDao.findByAssetnum(assetnum)
    }

    fun findAllAsset(): List<AssetEntity>? {
        return assetDao.findAllAsset()
    }

    fun findAllLocation(): List<LocationEntity>? {
        return locationDao.locationList()
    }

    fun findByTagCode(tagcode : String): AssetEntity? {
        return assetDao.findByTagCode(tagcode)
    }

    fun findByTagCodeStatus(tagcode : String, status : String): AssetEntity? {
        Timber.d("findByTagCode %s", tagcode)
        return assetDao.findByTagCodeAndStatus(tagcode, status)
    }



}