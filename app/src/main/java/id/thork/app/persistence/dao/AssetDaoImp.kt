package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseDao
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.AssetEntity_
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by Raka Putra on 5/11/21
 * Jakarta, Indonesia.
 */
class AssetDaoImp : AssetDao,BaseDao() {
    val TAG = AssetDaoImp::class.java.name

    var assetEntityBox: Box<AssetEntity>

    init {
        assetEntityBox = ObjectBox.boxStore.boxFor(AssetEntity::class.java)
    }

    private fun addUpdateInfo(assetEntity: AssetEntity, username: String?) {
        assetEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                assetEntity.createdDate = Date()
                assetEntity.createdBy = username
            }
        )
        assetEntity.updatedDate = Date()
        assetEntity.updatedBy = username
    }

    override fun remove() {
        assetEntityBox.removeAll()
    }

    override fun findAllAsset(): List<AssetEntity> {
        return assetEntityBox.query().notNull(AssetEntity_.assetnum).build()
            .find()
    }

    override fun createAssetCache(assetEntity: AssetEntity, username: String?): AssetEntity {
        addUpdateInfo(assetEntity, username)
        assetEntityBox.put(assetEntity)
        return assetEntity
    }

    override fun findByAssetnum(assetnum: String): AssetEntity? {
        val assetEntity =
            assetEntityBox.query().equal(AssetEntity_.assetnum, assetnum).build().find()
        assetEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findByTagCode(tagcode: String): AssetEntity? {
        val assetEntity =
            assetEntityBox.query().equal(AssetEntity_.assetrfid, tagcode).build().find()
        Timber.d("findByTagCode size query %s", assetEntity.size)
        assetEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null

    }

    override fun findByTagCodeAndStatus(tagcode: String, status: String): AssetEntity? {
        val assetEntity =
            assetEntityBox.query().equal(AssetEntity_.assetrfid, tagcode)
                .equal(AssetEntity_.status, status).build().find()
        Timber.d("findByTagCode size query %s", assetEntity.size)
        assetEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }
}