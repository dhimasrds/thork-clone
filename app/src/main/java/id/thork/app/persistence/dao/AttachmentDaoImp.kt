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

class AttachmentDaoImp: AttachmentDao {
    override fun save(attachmentEntity: AttachmentEntity, username: String) {
    }

    override fun save(attachmentEntities: List<AttachmentEntity>, username: String) {
    }

    override fun delete(id: Long) {
    }

    override fun delete() {
    }

    override fun fetchAttachmentByWoId(woId: Int) {
    }

    override fun fetchAttachmentByWoIdAndType(woId: Int, type: String) {
    }

    override fun findAttachment(id: Long) {

    }

    override fun findAttachmentBySyncStatus(syncStatus: Boolean) {

    }
}