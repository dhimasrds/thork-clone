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

import id.thork.app.persistence.entity.SyncEntity

interface SyncDao {
    fun save(syncEntity: SyncEntity, username: String): SyncEntity
    fun save(syncEntities: List<SyncEntity>, username: String)
    fun delete()
    fun findSync(id: Long): List<SyncEntity>
    fun findSyncs(): List<SyncEntity>
}