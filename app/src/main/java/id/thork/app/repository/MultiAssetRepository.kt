package id.thork.app.repository

import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.dao.MultiAssetDao
import id.thork.app.persistence.entity.MultiAssetEntity
import id.thork.app.utils.DateUtils
import javax.inject.Inject
/**
 * Created by Dhimas Saputra on 18/5/21
 * Jakarta, Indonesia.
 */
class MultiAssetRepository @Inject constructor(
    private val multiAssetDao: MultiAssetDao,
    private val preferenceManager: PreferenceManager,
    private val appSession: AppSession
) :BaseRepository() {

    fun getAllMultiAsset() : List<MultiAssetEntity>{
       return multiAssetDao.findAllMultiAsset()
    }

    fun getMultiAssetByAssetNum(assetnum : String): MultiAssetEntity? {
        return multiAssetDao.findMultiAssetByAssetnum(assetnum)
    }

    fun getMultiAssetByAssetNumAndParent(assetnum: String, parent: String): MultiAssetEntity? {
        return multiAssetDao.findMultiAssetByAssetnumAndParent(assetnum, parent)
    }

    fun getListMultiAssetByParent(wonum : String) : List<MultiAssetEntity> {
        return multiAssetDao.findListMultiAssetByParent(wonum)
    }

    fun updateMultiAsset(assetnum: String, parent: String, isScan: Int, scantype: String) {
        val multiAssetEntity = getMultiAssetByAssetNumAndParent(assetnum, parent)
        multiAssetEntity.whatIfNotNull {
            it.isScan = isScan
            it.scantype = scantype
            it.thisfsmtagtime = DateUtils.getDateTimeMaximo()
            multiAssetDao.save(it, appSession.userEntity.username.toString())
        }
    }
}