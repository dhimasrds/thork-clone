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

import id.thork.app.persistence.entity.LogEntity

interface LogDao {
    fun findLog(id: Long): LogEntity
    fun save(logEntity: LogEntity, username: String): LogEntity
    fun save(logEntities: MutableList<LogEntity>, username: String)
    fun save(trxId: String, message: String, username: String): LogEntity
    fun removeAll()

    fun findLogs(): List<LogEntity>
    fun pagingLog(position: Long ,limit: Long): List<LogEntity>?
    fun getLogById(id: Long): LogEntity?


}