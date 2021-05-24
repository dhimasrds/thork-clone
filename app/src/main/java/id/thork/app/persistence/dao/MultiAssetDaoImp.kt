package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.*
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 17/05/2021
 * Jakarta, Indonesia.
 */
class MultiAssetDaoImp : MultiAssetDao {
    val TAG = MultiAssetDaoImp::class.java.name

    var multiAssetEntityBox: Box<MultiAssetEntity>

    init {
        multiAssetEntityBox = ObjectBox.boxStore.boxFor(MultiAssetEntity::class.java)
    }

    private fun addUpdateInfo(multiAssetEntity: MultiAssetEntity, username: String) {
        multiAssetEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                multiAssetEntity.createdDate = Date()
                multiAssetEntity.createdBy = username
            }
        )
        multiAssetEntity.updatedDate = Date()
        multiAssetEntity.updatedBy = username
    }

    override fun save(
        multiAssetEntity: MultiAssetEntity,
        username: String
    ) {
        addUpdateInfo(multiAssetEntity, username)
        multiAssetEntityBox.put(multiAssetEntity)
    }

    override fun remove() {
        multiAssetEntityBox.removeAll()
    }

    override fun saveListMultiAsset(multiAssetEntityList: List<MultiAssetEntity>): List<MultiAssetEntity> {
        multiAssetEntityBox.put(multiAssetEntityList)
        return multiAssetEntityList
    }

    override fun findListMultiAssetByParent(wonum: String): List<MultiAssetEntity> {
        return multiAssetEntityBox.query().equal(MultiAssetEntity_.wonum, wonum).build().find()
    }

    override fun findMultiAssetByAssetnum(assetnum: String): MultiAssetEntity? {
        val multiAssetEntity: List<MultiAssetEntity> =
            multiAssetEntityBox.query().equal(MultiAssetEntity_.assetNum, assetnum).build().find()
        multiAssetEntity.whatIfNotNullOrEmpty { return multiAssetEntity[0] }
        return null
    }

    override fun findAllMultiAsset(): List<MultiAssetEntity> {
        return multiAssetEntityBox.query().notNull(MultiAssetEntity_.assetNum).build()
            .find()
    }

}