package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.MultiAssetEntity
import id.thork.app.persistence.entity.WoCacheEntity

/**
 * Created by M.Reza Sulaiman on 17/05/2021
 * Jakarta, Indonesia.
 */
interface MultiAssetDao {
    fun save(multiAssetEntity: MultiAssetEntity, username: String)
    fun remove()
    fun saveListMultiAsset(multiAssetEntityList: List<MultiAssetEntity>): List<MultiAssetEntity>
    fun findListMultiAssetByParent(parent: String): List<MultiAssetEntity>
    fun findAllMultiAsset(): List<MultiAssetEntity>

}