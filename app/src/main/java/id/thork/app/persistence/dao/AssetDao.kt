package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.WoCacheEntity

/**
 * Created by Raka Putra on 5/11/21
 * Jakarta, Indonesia.
 */
interface AssetDao {

    fun findAllAsset(): List<AssetEntity>

    fun createAssetCache(assetEntity: AssetEntity, username: String?): AssetEntity


}