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
import android.net.Uri
import android.view.LayoutInflater.from
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.databinding.AttachmentItemBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.GlideApp
import id.thork.app.pages.attachment.AttachmentActivity
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.utils.*
import timber.log.Timber


class AttachmentAdapter constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val requestOptions: RequestOptions,
    private val attachmentActivity: AttachmentActivity,
    private val attachmentEntities: List<AttachmentEntity>
) : RecyclerView.Adapter<AttachmentAdapter.AttachmentHolder>() {
    val TAG = AttachmentAdapter::class.java.name

    lateinit var attachmentEntity: AttachmentEntity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentHolder {
        val binding = DataBindingUtil.inflate<AttachmentItemBinding>(
            from(parent.getContext()),
            R.layout.attachment_item, parent, false
        )
        return AttachmentHolder(binding)
    }

    override fun onBindViewHolder(holder: AttachmentHolder, position: Int) {
        val attachmentEntity: AttachmentEntity = attachmentEntities[position]
        holder.bind(attachmentEntity)
    }

    override fun getItemCount(): Int = attachmentEntities.size

    inner class AttachmentHolder(val binding: AttachmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachmentEntity: AttachmentEntity) {
            with(binding) {
                tvAttachmentFilename.text = StringUtils.truncate(attachmentEntity.title, 30)
                tvAttachmentDesc.text = StringUtils.truncate(attachmentEntity.description, 30)
                classifyImageThumbnail(attachmentEntity, ivThumbnail)
                tvAttachmentTakenDate.text = DateUtils.getDateTimeOB(attachmentEntity.modifiedDate)

                root.setOnClickListener {
                    IntentUtils.displayData(context, attachmentEntity.uriString.toString())
                }
            }
        }

        private fun classifyImageThumbnail(
            attachmentEntity: AttachmentEntity,
            imageView: ImageView
        ) {
            attachmentEntity.whatIfNotNull { attachmentEntity ->
                var uri: Uri?
                attachmentEntity.uriString.whatIfNotNullOrEmpty {
                    Timber.tag(TAG).d("classifyImageThumbnail() uristring: %s", it)

                    if (it.startsWith("http")) {
                        attachmentEntity.mimeType?.let { mimeType ->
                            if (FileUtils.isImageType(mimeType)) {
                                val cookie: String =
                                    preferenceManager.getString(BaseParam.APP_MX_COOKIE)
                                Timber.tag(TAG).d("classifyImageThumbnail() cookies: %s", cookie)
                                val glideUrl = GlideUrl(
                                    it, LazyHeaders.Builder()
                                        .addHeader("Cookie", cookie)
                                        .build()
                                )
                                GlideApp.with(context).load(glideUrl)
                                    .apply(requestOptions)
                                    .into(imageView)
                            } else {
                                uri = fetchUri(mimeType)
                                GlideApp.with(context).load(uri)
                                    .apply(requestOptions)
                                    .into(imageView)
                            }
                        }
                    } else {
                        attachmentEntity.mimeType.whatIfNotNullOrEmpty { mimeType ->
                            val uri: Uri = fetchUri(it, mimeType)
                            Timber.tag(TAG).d("classifyImageThumbnail() thumbnail uri: %s", uri)
                            GlideApp.with(context).load(uri)
                                .apply(requestOptions)
                                .into(imageView)
                        }
                    }
                }
            }
        }

        private fun fetchUri(mimeType: String): Uri {
            val uri = if (FileUtils.isExcelType(mimeType)) {
                PathUtils.getDrawableUri(context, R.drawable.ic_excel_file)
            } else if (FileUtils.isWordType(mimeType)) {
                PathUtils.getDrawableUri(context, R.drawable.ic_word_file)
            } else if (FileUtils.isPdfType(mimeType)) {
                PathUtils.getDrawableUri(context, R.drawable.ic_pdf_file)
            } else {
                PathUtils.getDrawableUri(context, R.drawable.image_broken)
            }
            return uri
        }

        private fun fetchUri(uriString: String, mimeType: String): Uri {
            val uri = if (FileUtils.isImageType(mimeType)) {
                Uri.parse(uriString)
            } else if (FileUtils.isExcelType(mimeType)) {
                PathUtils.getDrawableUri(context, R.drawable.ic_excel_file)
            } else if (FileUtils.isWordType(mimeType)) {
                PathUtils.getDrawableUri(context, R.drawable.ic_word_file)
            } else if (FileUtils.isPdfType(mimeType)) {
                PathUtils.getDrawableUri(context, R.drawable.ic_pdf_file)
            } else {
                PathUtils.getDrawableUri(context, R.drawable.image_broken)
            }
            return uri
        }

    }
}