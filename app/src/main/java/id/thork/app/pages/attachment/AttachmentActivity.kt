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

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityAttachmentBinding
import id.thork.app.pages.attachment.element.AttachmentAdapter
import id.thork.app.pages.attachment.element.AttachmentViewModel
import id.thork.app.persistence.entity.AttachmentEntity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AttachmentActivity : BaseActivity() {
    val TAG = AttachmentActivity::class.java.name

    val viewModel: AttachmentViewModel by viewModels()
    private val binding: ActivityAttachmentBinding by binding(R.layout.activity_attachment)

    private lateinit var attachmentAdapter: AttachmentAdapter
    private lateinit var attachmentEntities: MutableList<AttachmentEntity>

    @Inject
    @Named("svgRequestOption")
    lateinit var svgRequestOptions: RequestOptions

    override fun setupView() {
        super.setupView()
        attachmentEntities = mutableListOf()
        attachmentAdapter = AttachmentAdapter(this, svgRequestOptions, this, attachmentEntities)

        binding.apply {
            lifecycleOwner = this@AttachmentActivity
            vm = viewModel

            rvAttachments.apply {
                layoutManager = LinearLayoutManager(this@AttachmentActivity)
                addItemDecoration(
                    DividerItemDecoration(
                        this@AttachmentActivity,
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = attachmentAdapter
            }
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.create_wo),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = true
        )

        viewModel.fetchAttachments(1)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.attachments.observe(this, {
            attachmentEntities.clear()
            attachmentEntities.addAll(it)
            attachmentAdapter.notifyDataSetChanged()
            Timber.tag(TAG).d("setupObserver() size: %s", attachmentEntities.size)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            binding.ivThumbnail.setImageURI(uri)
            Timber.tag(TAG).d("onActivityResult() camera uri: %s", uri)
        } else if (resultCode == ImagePicker.REQUEST_CODE) {
            val uri: Uri = data?.data!!
            binding.ivThumbnail.setImageURI(uri)
            Timber.tag(TAG).d("onActivityResult() gallery uri: %s", uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Timber.tag(TAG).d("onActivityResult() error: %s", ImagePicker.getError(data))
        } else {
            Timber.tag(TAG).d("onActivityResult() cancel")
        }
    }
}