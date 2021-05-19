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

package id.thork.app.pages.attachment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityAttachmentBinding
import id.thork.app.pages.attachment.element.AttachmentAdapter
import id.thork.app.pages.attachment.element.AttachmentViewModel
import id.thork.app.persistence.entity.AttachmentEntity
import timber.log.Timber

class AttachmentActivity : BaseActivity() {
    val TAG = AttachmentActivity::class.java.name

    val attachmentViewModel: AttachmentViewModel by viewModels()
    private val binding: ActivityAttachmentBinding by binding(R.layout.activity_attachment)

    private lateinit var attachmentAdapter: AttachmentAdapter
    private lateinit var attachmentEntities: MutableList<AttachmentEntity>

    override fun setupView() {
        super.setupView()
        attachmentEntities = mutableListOf()
        attachmentAdapter = AttachmentAdapter(this, this, attachmentEntities)

        binding.apply {
            lifecycleOwner = this@AttachmentActivity
            vm = attachmentViewModel

            rvAttachments.apply {
                layoutManager = LinearLayoutManager(this@AttachmentActivity)
                addItemDecoration(DividerItemDecoration(this@AttachmentActivity, LinearLayoutManager.VERTICAL))
                adapter = attachmentAdapter
            }
        }
        attachmentViewModel.fetchAttachments(1)
    }

    override fun setupObserver() {
        super.setupObserver()
        attachmentViewModel.attachments.observe(this, {
            attachmentEntities.clear()
            attachmentEntities.addAll(it)
            attachmentAdapter.notifyDataSetChanged()
            Timber.tag(TAG).d("setupObserver() size: %s", attachmentEntities.size)
        })
    }

}