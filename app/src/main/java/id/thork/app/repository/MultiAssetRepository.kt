package id.thork.app.repository

import id.thork.app.base.BaseRepository
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.dao.MultiAssetDao
import id.thork.app.persistence.entity.MultiAssetEntity
import javax.inject.Inject
/**
 * Created by Dhimas Saputra on 18/5/21
 * Jakarta, Indonesia.
 */
class MultiAssetRepository @Inject constructor(
    private val multiAssetDao: MultiAssetDao,
    private val preferenceManager: PreferenceManager
) :BaseRepository {

    fun getAllMultiAsset() : List<MultiAssetEntity>{
       return multiAssetDao.findAllMultiAsset()
    }

    fun getMultiAssetByAssetNum(assetnum : String): MultiAssetEntity? {
        return multiAssetDao.findMultiAssetByAssetnum(assetnum)
    }
}