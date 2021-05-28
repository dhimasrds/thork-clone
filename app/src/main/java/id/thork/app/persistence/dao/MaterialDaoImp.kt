package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.MultiAssetEntity
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 28/05/2021
 * Jakarta, Indonesia.
 */
class MaterialDaoImp : MaterialDao {
    val TAG = MaterialDaoImp::class.java.name


    var materialEntityBox: Box<MaterialEntity>

    init {
        materialEntityBox = ObjectBox.boxStore.boxFor(MaterialEntity::class.java)
    }

    private fun addUpdateInfo(materialEntity: MaterialEntity, username: String) {
        materialEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                materialEntity.createdDate = Date()
                materialEntity.createdBy = username
            }
        )
        materialEntity.updatedDate = Date()
        materialEntity.updatedBy = username
    }

    override fun save(
        materialEntity: MaterialEntity,
        username: String
    ) {
        addUpdateInfo(materialEntity, username)
        materialEntityBox.put(materialEntity)
    }

    override fun remove() {
        materialEntityBox.removeAll()
    }



}