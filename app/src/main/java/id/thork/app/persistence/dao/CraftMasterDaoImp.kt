package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.CraftMasterEntity
import id.thork.app.persistence.entity.CraftMasterEntity_
import io.objectbox.Box
import io.objectbox.query.PropertyQuery
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 03/08/2021
 * Jakarta, Indonesia.
 */
class CraftMasterDaoImp : CraftMasterDao {
    val TAG = CraftMasterDaoImp::class.java.name

    var craftMasterEntityBox: Box<CraftMasterEntity>

    init {
        craftMasterEntityBox = ObjectBox.boxStore.boxFor(CraftMasterEntity::class.java)
    }

    private fun addUpdateInfo(craftMasterEntity: CraftMasterEntity, username: String?) {
        craftMasterEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                craftMasterEntity.createdDate = Date()
                craftMasterEntity.createdBy = username
            }
        )
        craftMasterEntity.updatedDate = Date()
        craftMasterEntity.updatedBy = username
    }

    override fun remove() {
        craftMasterEntityBox.removeAll()
    }

    override fun createCraftCache(craftMasterEntity: CraftMasterEntity, username: String?) {
        addUpdateInfo(craftMasterEntity, username)
        craftMasterEntityBox.put(craftMasterEntity)

    }

    override fun getListCraftByLaborcode(laborcode: String): List<CraftMasterEntity> {
        return craftMasterEntityBox.query()
            .equal(CraftMasterEntity_.laborcode, laborcode)
            .build().find()
    }

    override fun getCraftByLaborcode(
        laborcode: String
    ): CraftMasterEntity? {
        val craftEntity =
            craftMasterEntityBox.query().equal(CraftMasterEntity_.laborcode, laborcode)
                .build()
                .find()
        craftEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun getCraft() : Array<out String>? {
        return craftMasterEntityBox.query().build().property(CraftMasterEntity_.craft).distinct().findStrings()

    }
}