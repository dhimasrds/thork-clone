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
class StoreroomDaoImp : StoreroomDao, BaseDao() {
    val TAG = StoreroomDaoImp::class.java.name

    var storeroomEntityBox: Box<StoreroomEntity>

    init {
        storeroomEntityBox = ObjectBox.boxStore.boxFor(StoreroomEntity::class.java)
    }

    override fun save(storeroomEntity: StoreroomEntity, username: String) {
        addUpdateInfo(storeroomEntity, username)
        storeroomEntityBox.put(storeroomEntity)
    }

    override fun save(storeroomEntities: List<StoreroomEntity>, username: String) {
        for (storeroomEntity in storeroomEntities) {
            addUpdateInfo(storeroomEntity, username)
        }
        storeroomEntityBox.put(storeroomEntities)
    }
    override fun delete(storeroomEntity: StoreroomEntity) {
        storeroomEntityBox.remove(storeroomEntity)
    }

    override fun delete() {
        storeroomEntityBox.removeAll()
    }

    override fun findStorerooms(): List<StoreroomEntity> {
        return storeroomEntityBox.query().build().find()
    }

    override fun findStoreroom(location: String): List<StoreroomEntity> {
        return storeroomEntityBox.query()
            .equal(StoreroomEntity_.location, location)
            .build().find()
    }

}