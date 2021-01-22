package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.persistence.entity.WoCacheEntity_
import java.util.*

/**
 * Created by Dhimas Saputra on 21/01/21
 * Jakarta, Indonesia.
 */
class WoCacheDaoImp : WoCacheDao {
    private var woCacheEntityBox = ObjectBox.boxStore.boxFor(WoCacheEntity::class.java)


    private fun addUpdateInfo(woCacheEntity: WoCacheEntity, username: String) {
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
        addUpdateInfo(woCacheEntity, username!!)
        woCacheEntityBox.put(woCacheEntity)
        return woCacheEntity
    }

    override fun findWoByWonum(wonum: String): WoCacheEntity {
        TODO("Not yet implemented")
    }

    override fun findWoByWonumAndStatus(wonum: String, status: String?): WoCacheEntity {
        TODO("Not yet implemented")
    }

    override fun findWoByWonumAndIsLatest(wonum: String, isLatest: Int): WoCacheEntity {
        TODO("Not yet implemented")
    }

    override fun findWoisLatestByWonumAndStatus(wonum: String, status: String?): WoCacheEntity {
        TODO("Not yet implemented")
    }

    override fun findAllWo(): List<WoCacheEntity> {
        TODO("Not yet implemented")
    }

    override fun findAllWo(offset: Int): List<WoCacheEntity> {
        return woCacheEntityBox.query().notNull(WoCacheEntity_.syncBody).build()
            .find(offset.toLong(), 10)
    }

    override fun findWoBySyncAndChanged(sync: Int, changed: Int): List<WoCacheEntity> {
        TODO("Not yet implemented")
    }

    override fun removeAllWo() {
        woCacheEntityBox.removeAll()
    }

    override fun removeByTWoCacheEntity(tWoCacheEntity: WoCacheEntity) {
        TODO("Not yet implemented")
    }

    override fun updateWo(woCacheEntity: WoCacheEntity, username: String) {
        TODO("Not yet implemented")
    }

    override fun findApprWo(): WoCacheEntity? {
        TODO("Not yet implemented")
    }
}