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

package id.thork.app.pages.attachment.element

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.pages.login.element.LoginViewModel
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.utils.PathUtils
import timber.log.Timber

class AttachmentViewModel @ViewModelInject constructor(
    private val context: Context,
    private val appSession: AppSession,
) : LiveCoroutinesViewModel() {
    val TAG = AttachmentViewModel::class.java.name

    private val _attachments = MutableLiveData<List<AttachmentEntity>>()
    val attachments: LiveData<List<AttachmentEntity>> get() = _attachments

    private lateinit var attachmentEntities: MutableList<AttachmentEntity>

    fun fetchAttachments(woId: Int) {
        Timber.tag(TAG).d("fetchAttachments() woId: %s", woId)
        attachmentEntities = mutableListOf(
            AttachmentEntity(name = "ikankoi.docccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc", mimeType = "WORD"),
            AttachmentEntity(name = "ikanmas.pdf", mimeType = "PDF"),
//            AttachmentEntity(name = "ikanlele.docx", type = "WORD"),
//            AttachmentEntity(name = "ikanpaus.jpg", type = "IMAGE"),
//            AttachmentEntity(name = "ikanlele.xlsx", type = "EXCEL"),
//            AttachmentEntity(name = "ikanpaus.jpg", type = "IMAGE"),
            AttachmentEntity(name = "filepdf.pdf", mimeType = "PDF",
                uriString = PathUtils.getResourceUri(context, "assets/filepdf.pdf").toString())
        )
        _attachments.value = attachmentEntities
    }

    fun addItem(attachmentEntity: AttachmentEntity) {
        attachmentEntities.add(attachmentEntity)
        _attachments.value = attachmentEntities
    }

}