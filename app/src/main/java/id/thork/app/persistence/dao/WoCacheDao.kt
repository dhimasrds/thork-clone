package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.WoCacheEntity

/**
 * Created by Dhimas Saputra on 21/01/21
 * Jakarta, Indonesia.
 */
interface WoCacheDao {

    fun createWoCache(woCacheEntity: WoCacheEntity,username: String?): WoCacheEntity

    fun findWoByWonum(wonum: String): WoCacheEntity

    fun findWoByWonumAndStatus(wonum: String, status: String?): WoCacheEntity

    fun findWoByWonumAndIsLatest(wonum: String, isLatest: Int): WoCacheEntity

    fun findWoisLatestByWonumAndStatus(wonum: String, status: String?): WoCacheEntity

    fun findAllWo(): List<WoCacheEntity>

    fun findWoBySyncAndChanged(sync: Int, changed: Int): List<WoCacheEntity>

    fun removeAllWo()

    fun removeByTWoCacheEntity(tWoCacheEntity: WoCacheEntity)

    fun updateWo(woCacheEntity: WoCacheEntity, username: String)

    fun findApprWo(): WoCacheEntity?
}