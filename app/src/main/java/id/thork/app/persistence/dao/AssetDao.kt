package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.AssetEntity

/**
 * Created by Raka Putra on 5/11/21
 * Jakarta, Indonesia.
 */
interface AssetDao {

    fun findAllAsset(): List<AssetEntity>

    fun createAssetCache(assetEntity: AssetEntity, username: String?): AssetEntity

    fun remove()
    fun findByAssetnum(assetnum: String): AssetEntity?
    fun findByTagCode(tagcode: String): AssetEntity?
    fun findByTagCodeAndStatus(tagcode: String, status: String): AssetEntity?
}