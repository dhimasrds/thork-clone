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

package id.thork.app.pages.attachment.element.image_preview

import android.net.Uri
import androidx.activity.viewModels
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityAttachmentImagePreviewBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.GlideApp
import timber.log.Timber
import javax.inject.Inject

class AttachmentImagePreviewActivity : BaseActivity() {
    val TAG = AttachmentImagePreviewActivity::class.java.name

    private var intentUri: String? = null

    val viewModel: AttachmentImagePreviewViewModel by viewModels()
    private val binding: ActivityAttachmentImagePreviewBinding by binding(R.layout.activity_attachment_image_preview)

    @Inject
    lateinit var requestOptions: RequestOptions

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun setupView() {
        super.setupView()

        binding.apply {
            lifecycleOwner = this@AttachmentImagePreviewActivity
            vm = viewModel
        }
        retrieveFromIntent()
    }

    private fun retrieveFromIntent() {
        intentUri = intent.getStringExtra(BaseParam.ATTACHMENTURI)
        Timber.d("retrieveFromIntent() intentUri: %s", intentUri)
        intentUri?.let {
            previewImage(it)
        }
    }

    private fun previewImage(uriString: String) {
        uriString.let {
            if (uriString.startsWith("http")) {
                val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
                Timber.tag(TAG).d("previewImage() cookies: %s", cookie)
                val glideUrl = GlideUrl(
                    it, LazyHeaders.Builder()
                        .addHeader("Cookie", cookie)
                        .build()
                )
                GlideApp.with(this).load(glideUrl)
                    .apply(requestOptions)
                    .into(binding.ivPreview)
            } else {
                val uri = Uri.parse(uriString)
                Timber.tag(TAG).d("previewImage() uri: %s", uri.toString())
                GlideApp.with(this).load(uri)
                    .apply(requestOptions)
                    .into(binding.ivPreview)
            }
        }
    }
}