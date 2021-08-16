/*
 * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * This.ID. ("Confidential Information").
 *
 * Such Confidential Information shall not be disclosed and shall
 * use it only	 in accordance with the terms of the license agreement
 * entered into with This.ID; other than in accordance with the written
 * permission of This.ID.
 */

package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseDao
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.*
import io.objectbox.Box
import io.objectbox.kotlin.equal
import io.objectbox.query.QueryBuilder
import java.util.*

class SyncDaoImp : SyncDao, BaseDao() {
    var syncEntityBox: Box<SyncEntity>

    init {
        syncEntityBox = ObjectBox.boxStore.boxFor(SyncEntity::class.java)
    }

    override fun save(syncEntity: SyncEntity, username: String): SyncEntity {
        addUpdateInfo(syncEntity, username)
        syncEntityBox.put(syncEntity)
        return syncEntity
    }

    override fun save(syncEntities: List<SyncEntity>, username: String) {
        for (syncEntity in syncEntities) {
            addUpdateInfo(syncEntity, username)
        }
        syncEntityBox.put(syncEntities)
    }

    override fun delete() {
        syncEntityBox.removeAll()
    }

    override fun findSync(id: Long): List<SyncEntity> {
        return syncEntityBox.query()
            .equal(SyncEntity_.id, id).build().find()
        return listOf()
    }

    override fun findSyncs(): List<SyncEntity> {
        return syncEntityBox.query()
            .orderDesc(SyncEntity_.syncDate).build().find()
        return listOf()
    }
}
