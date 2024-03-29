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

import id.thork.app.persistence.entity.AttachmentEntity

interface AttachmentDao {
    fun save(attachmentEntity: AttachmentEntity, username: String)
    fun save(attachmentEntities: List<AttachmentEntity>, username: String)
    fun delete(id: Long)
    fun delete()
    fun fetchAttachmentByWoId(woId: Int): List<AttachmentEntity>
    fun fetchAttachmentByWoIdAndType(woId: Int, type: String): List<AttachmentEntity>
    fun findAttachment(id: Long): AttachmentEntity
    fun findAttachmentBySyncStatus(syncStatus: Boolean): List<AttachmentEntity>

    fun fetchAttachmentByWoIdAndSyncStatus(woId: Int, syncStatus: Boolean): List<AttachmentEntity>
}