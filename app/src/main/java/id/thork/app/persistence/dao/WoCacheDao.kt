package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.WoCacheEntity

/**
 * Created by Dhimas Saputra on 21/01/21
 * Jakarta, Indonesia.
 */
interface WoCacheDao {

    fun createWoCache(woCacheEntity: WoCacheEntity, username: String?): WoCacheEntity

    fun findWoByWonum(wonum: String): WoCacheEntity?

    fun findWoByWonumAndStatus(wonum: String, status: String?): WoCacheEntity?

    fun findWoisLatestByWonumAndStatus(wonum: String, status: String?): WoCacheEntity

    fun findAllWo(): List<WoCacheEntity>

    fun findWoBySyncAndChanged(sync: Int, changed: Int): List<WoCacheEntity>

    fun removeAllWo()

    fun removeByTWoCacheEntity(tWoCacheEntity: WoCacheEntity)

    fun updateWo(woCacheEntity: WoCacheEntity, username: String)

    fun findApprWo(): WoCacheEntity?

    fun findAllWo(offset: Int): List<WoCacheEntity>

    fun findWoByWonum(offset: Int, wonum: String, status: String): List<WoCacheEntity>
    fun findListWoByStatus(status: String): List<WoCacheEntity>
    fun findListWoByStatusOffset( offset: Int, vararg status: String): List<WoCacheEntity>
    fun findListWoByStatusOffsetAndRfid( offset: Int, vararg status: String): List<WoCacheEntity>
    fun findWoByWonumComp(offset: Int, wonum: String, status: String): List<WoCacheEntity>
    fun remove()

    fun findWoByisLatest(isLatest: Int, offset: Int): List<WoCacheEntity>
    fun findWoByWonumAndIslatest(wonum: String, isLatest: Int): WoCacheEntity?
    fun findWoListBySyncStatusAndisChange(syncStatus: Int, isChange: Int): List<WoCacheEntity>
    fun findListWoByStatusHistory(vararg status: String): List<WoCacheEntity>
    fun findWoByWonumAndIsChange(wonum: String, isChange: Int): WoCacheEntity?

    fun findWoByWoId(woid: Int): WoCacheEntity?
}