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
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.LogEntity
import id.thork.app.persistence.entity.LogEntity_
import io.objectbox.Box
import io.objectbox.query.QueryBuilder
import java.util.*

class LogDaoImp : LogDao {
    var logEntityBox: Box<LogEntity>

    init {
        logEntityBox = ObjectBox.boxStore.boxFor(LogEntity::class.java)
    }

    override fun findLogs(): List<LogEntity> {
        return logEntityBox.query()
            .order(LogEntity_.createdDate, QueryBuilder.DESCENDING or QueryBuilder.CASE_SENSITIVE)
            .build().find()
    }

    override fun findLog(id: String): LogEntity {
        val logEntities: List<LogEntity>  =
            logEntityBox.query().equal(LogEntity_.id, id).build().find()
        return logEntities[0]
    }

    override fun save(logEntity: LogEntity, username: String): LogEntity {
        addUpdateInfo(logEntity, username)
        logEntityBox.put(logEntity)
        return logEntity
    }

    override fun save(trxId: String, message: String, username: String): LogEntity {
        val logEntity = LogEntity(trxId, message)
        addUpdateInfo(logEntity, username)
        logEntityBox.put(logEntity)
        return logEntity
    }
    override fun removeAll() {
        logEntityBox.removeAll()
    }

    private fun addUpdateInfo(logEntity: LogEntity, username: String) {
        logEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                logEntity.createdDate = Date()
                logEntity.createdBy = username
            }
        )
        logEntity.updatedDate = Date()
        logEntity.updatedBy = username
    }
}
