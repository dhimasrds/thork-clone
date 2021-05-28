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
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import id.thork.app.example.Person
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.persistence.entity.AttachmentEntity_
import id.thork.app.persistence.entity.LogEntity
import id.thork.app.persistence.entity.UserEntity
import io.objectbox.kotlin.equal
import timber.log.Timber
import java.util.*

class AttachmentDaoImp : AttachmentDao {
    val TAG = AttachmentDaoImp::class.java.name

    private var attachmentEntityBox = ObjectBox.boxStore.boxFor(AttachmentEntity::class.java)

    private val moshi: Moshi
    private val jsonAdapter: JsonAdapter<AttachmentEntity>
    private val logDao: LogDao

    init {
        moshi = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()
        jsonAdapter = moshi.adapter(AttachmentEntity::class.java)

        logDao = LogDaoImp()
    }

    override fun save(attachmentEntity: AttachmentEntity, username: String) {
        addUpdateInfo(attachmentEntity, username)
        attachmentEntityBox.put(attachmentEntity)
        saveLog(attachmentEntity, username)
    }

    override fun save(attachmentEntities: List<AttachmentEntity>, username: String) {
        attachmentEntities.forEach {
            addUpdateInfo(it, username)
        }
        attachmentEntityBox.put(attachmentEntities)
        saveLog(attachmentEntities, username)
    }

    override fun delete(id: Long) {
        attachmentEntityBox.remove(id)
    }

    override fun delete() {
        attachmentEntityBox.removeAll()
    }

    override fun fetchAttachmentByWoId(woId: Int): List<AttachmentEntity> {
        val attachmentEntities =
            attachmentEntityBox.query().equal(AttachmentEntity_.workOrderId, woId)
                .build().find()
        return attachmentEntities
    }

    override fun fetchAttachmentByWoIdAndType(woId: Int, type: String): List<AttachmentEntity> {
        val attachmentEntities =
            attachmentEntityBox.query().equal(AttachmentEntity_.workOrderId, woId)
                .and().equal(AttachmentEntity_.docType, type)
                .build().find()
        return attachmentEntities
    }

    override fun fetchAttachmentByWoIdAndSyncStatus(woId: Int, syncStatus: Boolean): List<AttachmentEntity> {
        val attachmentEntities =
            attachmentEntityBox.query().equal(AttachmentEntity_.workOrderId, woId)
                .and().equal(AttachmentEntity_.syncStatus, syncStatus)
                .build().find()
        return attachmentEntities
    }

    override fun findAttachment(id: Long): AttachmentEntity {
        return attachmentEntityBox.get(id)
    }

    override fun findAttachmentBySyncStatus(syncStatus: Boolean): List<AttachmentEntity> {
        val attachmentEntities =
            attachmentEntityBox.query().equal(AttachmentEntity_.syncStatus, syncStatus)
                .build().find()
        return attachmentEntities
    }

    private fun addUpdateInfo(attachmentEntity: AttachmentEntity, username: String) {
        attachmentEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
               Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                attachmentEntity.createdDate = Date()
                attachmentEntity.createdBy = username
            }
        )
        attachmentEntity.updatedDate = Date()
        attachmentEntity.updatedBy = username
    }

    private fun saveLog(attachmentEntity: AttachmentEntity, username: String) {
        logDao.save(TAG, jsonAdapter.toJson(attachmentEntity), username)
    }

    private fun saveLog(attachmentEntities: List<AttachmentEntity>, username: String) {
        val logEntities: MutableList<LogEntity> = mutableListOf()
        attachmentEntities.forEach {
            val logEntity = LogEntity(trxId = TAG, message = jsonAdapter.toJson(it))
            logEntities.add(logEntity)
        }
        logDao.save(logEntities, username)
    }

}