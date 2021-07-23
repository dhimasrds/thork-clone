package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.initializer.ObjectBox.boxStore
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.persistence.entity.WoCacheEntity_
import io.objectbox.Box
import io.objectbox.kotlin.equal
import io.objectbox.query.OrderFlags
import io.objectbox.query.QueryBuilder
import java.util.*

/**
 * Created by Dhimas Saputra on 21/01/21
 * Jakarta, Indonesia.
 */
class WoCacheDaoImp : WoCacheDao {
    private var woCacheEntityBox = ObjectBox.boxStore.boxFor(WoCacheEntity::class.java)


    private fun addUpdateInfo(woCacheEntity: WoCacheEntity, username: String?) {
        woCacheEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                woCacheEntity.createdDate = Date()
                woCacheEntity.createdBy = username
            }
        )
        woCacheEntity.updatedDate = Date()
        woCacheEntity.updatedBy = username
    }

    override fun createWoCache(woCacheEntity: WoCacheEntity, username: String?): WoCacheEntity {
        addUpdateInfo(woCacheEntity, username)
        woCacheEntityBox.put(woCacheEntity)
        return woCacheEntity
    }

    override fun findWoByWonum(offset: Int, wonum: String, status: String): List<WoCacheEntity> {
        val woCacheBox: Box<WoCacheEntity> = boxStore.boxFor(WoCacheEntity::class.java)
        val woCacheEntity: List<WoCacheEntity> =
            woCacheBox.query().notEqual(WoCacheEntity_.status, status)
                .contains(WoCacheEntity_.wonum, wonum).build().find(
                    offset.toLong(),
                    10
                )
        return if (woCacheEntity.isNotEmpty()) {
            woCacheEntity
        } else emptyList()
    }

    override fun findWoByWonumComp(
        offset: Int,
        wonum: String,
        status: String
    ): List<WoCacheEntity> {
        val woCacheBox: Box<WoCacheEntity> = boxStore.boxFor(WoCacheEntity::class.java)
        val woCacheEntity: List<WoCacheEntity> =
            woCacheBox.query().equal(WoCacheEntity_.status, status)
                .order(WoCacheEntity_.reportDateUTCTime, QueryBuilder.DESCENDING or QueryBuilder.CASE_SENSITIVE)
                .contains(WoCacheEntity_.wonum, wonum).build().find(
                    offset.toLong(),
                    10
                )
        return if (woCacheEntity.isNotEmpty()) {
            woCacheEntity
        } else emptyList()
    }

    override fun findWoByWonum(wonum: String): WoCacheEntity? {
        val woCacheEntity: List<WoCacheEntity> =
            woCacheEntityBox.query().equal(WoCacheEntity_.wonum, wonum).build().find()
        woCacheEntity.whatIfNotNullOrEmpty { return woCacheEntity[0] }
        return null
    }

    override fun findWoByWonumAndIslatest(wonum: String, isLatest: Int): WoCacheEntity? {
        val woCacheEntity: List<WoCacheEntity> =
            woCacheEntityBox.query().equal(WoCacheEntity_.wonum, wonum).equal(WoCacheEntity_.isLatest, isLatest).build().find()
        woCacheEntity.whatIfNotNullOrEmpty { return woCacheEntity[0] }
        return null
    }

    override fun findWoByWonumAndStatus(wonum: String, status: String?): WoCacheEntity? {
        status.whatIfNotNullOrEmpty {
            val woCacheEntities: List<WoCacheEntity> =
                woCacheEntityBox.query().equal(WoCacheEntity_.wonum, wonum)
                    .equal(WoCacheEntity_.status, it).build().find()
            woCacheEntities.whatIfNotNullOrEmpty {
                return it[0]
            }
        }
        return null
    }

    override fun findWoisLatestByWonumAndStatus(wonum: String, status: String?): WoCacheEntity {
        TODO("Not yet implemented")
    }

    override fun findAllWo(): List<WoCacheEntity> {
        return woCacheEntityBox.query().notNull(WoCacheEntity_.syncBody).build()
            .find()
    }

    override fun findAllWo(offset: Int): List<WoCacheEntity> {
        return woCacheEntityBox.query().notNull(WoCacheEntity_.syncBody).build()
            .find(offset.toLong(), 10)
    }

    override fun findWoBySyncAndChanged(sync: Int, changed: Int): List<WoCacheEntity> {
        TODO("Not yet implemented")
    }

    override fun findWoByisLatest(isLatest: Int, offset: Int): List<WoCacheEntity> {
        return woCacheEntityBox.query().equal(WoCacheEntity_.isLatest, isLatest).build().find(offset.toLong(), 10)

    }

    override fun findWoListBySyncStatusAndisChange(syncStatus: Int, isChange: Int): List<WoCacheEntity> {
        return woCacheEntityBox.query().equal(WoCacheEntity_.syncStatus, syncStatus).equal(WoCacheEntity_.isChanged, isChange).build().find()

    }

    override fun removeAllWo() {
        woCacheEntityBox.removeAll()
    }

    override fun removeByTWoCacheEntity(tWoCacheEntity: WoCacheEntity) {
        TODO("Not yet implemented")
    }

    override fun updateWo(woCacheEntity: WoCacheEntity, username: String) {
        val woCacheBox: Box<WoCacheEntity> = boxStore.boxFor(WoCacheEntity::class.java)
        addUpdateInfo(woCacheEntity, username)
        woCacheBox.put(woCacheEntity)
    }

    override fun findApprWo(): WoCacheEntity? {
        TODO("Not yet implemented")
    }

    override fun findListWoByStatus(status: String): List<WoCacheEntity> {
        return woCacheEntityBox.query().equal(WoCacheEntity_.status, status).build().find()
    }

    override fun findListWoByStatusOffset( offset: Int, vararg status: String): List<WoCacheEntity> {
        return woCacheEntityBox.query()
            .`in`(WoCacheEntity_.status, status).build()
            .find(offset.toLong(), 10)
    }

    override fun remove() {
        woCacheEntityBox.removeAll()
    }

    override fun findListWoByStatusHistory(vararg status: String): List<WoCacheEntity> {
        return woCacheEntityBox.query().`in`(WoCacheEntity_.status, status).build().find()
    }

    override fun findWoByWonumAndIsChange(wonum: String, isChange: Int): WoCacheEntity? {
        val woCacheEntity: List<WoCacheEntity> =
            woCacheEntityBox.query().equal(WoCacheEntity_.wonum, wonum).equal(WoCacheEntity_.isChanged, isChange).build().find()
        woCacheEntity.whatIfNotNullOrEmpty { return woCacheEntity[0] }
        return null
    }
}