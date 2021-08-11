package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseDao
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.*
import io.objectbox.Box
import io.objectbox.kotlin.equal
import timber.log.Timber
import java.util.*

/**
 * Created by Reja on 11/08/2021
 * Jakarta, Indonesia.
 */
class MaterialStoreroomDaoImp : MaterialStoreroomDao, BaseDao() {
    val TAG = MaterialStoreroomDaoImp::class.java.name

    var matStoreroomEntityBox: Box<MaterialStoreroomEntity>

    init {
        matStoreroomEntityBox = ObjectBox.boxStore.boxFor(MaterialStoreroomEntity::class.java)
    }

    override fun save(materialStoreroomEntity: MaterialStoreroomEntity, username: String) {
        addUpdateInfo(materialStoreroomEntity, username)
        matStoreroomEntityBox.put(materialStoreroomEntity)
    }

    override fun save(materialStoreroomEntities: List<MaterialStoreroomEntity>, username: String) {
        for (matStoreroomEntity in materialStoreroomEntities) {
            addUpdateInfo(matStoreroomEntity, username)
        }
        matStoreroomEntityBox.put(materialStoreroomEntities)
    }
    override fun delete(materialStoreroomEntity: MaterialStoreroomEntity) {
        matStoreroomEntityBox.remove(materialStoreroomEntity)
    }

    override fun delete() {
        matStoreroomEntityBox.removeAll()
    }

    override fun findStorerooms(): List<MaterialStoreroomEntity> {
        return matStoreroomEntityBox.query().build().find()
    }

    override fun findStorerooms(itemNum: String): List<MaterialStoreroomEntity> {
        return matStoreroomEntityBox.query()
            .equal(MaterialStoreroomEntity_.itemNum, itemNum)
            .build().find()
    }

}