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
import com.bumptech.glide.request.RequestOptions
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.databinding.AttachmentItemBinding
import id.thork.app.network.GlideApp
import id.thork.app.pages.attachment.AttachmentActivity
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.utils.*


class AttachmentAdapter constructor(
    private val context: Context,
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
                tvAttachmentFilename.text = StringUtils.truncate(attachmentEntity.name, 30)
                tvAttachmentDesc.text = StringUtils.truncate(attachmentEntity.description, 30)
                classifyImageThumbnail(attachmentEntity, ivThumbnail)
                tvAttachmentTakenDate.text = DateUtils.getDateTimeOB(attachmentEntity.takenDate)

                root.setOnClickListener {
                    IntentUtils.displayData(context, attachmentEntity.uriString.toString())
                }
            }
        }

        private fun classifyImageThumbnail(
            attachmentEntity: AttachmentEntity,
            imageView: ImageView
        ) {
            var uri: Uri = PathUtils.getDrawableUri(context, R.drawable.image_broken)
            attachmentEntity.mimeType.whatIfNotNullOrEmpty {
                if (FileUtils.isImageType(it)) {
                    uri = Uri.parse(attachmentEntity.uriString.toString())
                } else if (FileUtils.isExcelType(it)) {
                    uri = PathUtils.getDrawableUri(context, R.drawable.ic_excel_file)
                } else if (FileUtils.isWordType(it)) {
                    uri = PathUtils.getDrawableUri(context, R.drawable.ic_word_file)
                } else if (FileUtils.isPdfType(it)) {
                    uri = PathUtils.getDrawableUri(context, R.drawable.ic_pdf_file)
                }
            }

            GlideApp.with(context).load(uri)
                .apply(requestOptions)
                .into(imageView)
        }
    }
}
