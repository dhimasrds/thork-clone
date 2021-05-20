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
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.databinding.AttachmentItemBinding
import id.thork.app.network.GlideApp
import id.thork.app.pages.attachment.AttachmentActivity
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.utils.PathUtils

class AttachmentAdapter constructor(
    private val context: Context,
    private val requestOptions: RequestOptions,
    private val attachmentActivity: AttachmentActivity,
    private val attachmentEntities: List<AttachmentEntity>
) : RecyclerView.Adapter<AttachmentAdapter.AttachmentHolder>() {

    lateinit var attachmentEntity: AttachmentEntity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentHolder {
        val layoutInflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val adapterView = layoutInflater.inflate(R.layout.attachment_item, parent, false)
//        layoutInflater.inflate(R.layout.attachment_item, parent, false)
//        LayoutInflater.from(context).inflate(R.layout.attachment_item, parent, false)

        val inflater = LayoutInflater.from(parent.context, )
        val binding = AttachmentItemBinding.inflate(inflater)
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
                tvAttachmentFilename.text = attachmentEntity.name
                classifyImageThumbnail(attachmentEntity, ivThumbnail)
            }
        }

        private fun classifyImageThumbnail(
            attachmentEntity: AttachmentEntity,
            imageView: ImageView
        ) {
            var uri: Uri = PathUtils.getDrawableUri(context, R.drawable.image_broken)

            when (attachmentEntity.type) {
                BaseParam.ATTACHMENT_TYPE_IMAGE -> {
                    uri = PathUtils.getDrawableUri(context, R.drawable.ic_image_file)
                }
                BaseParam.ATTACHMENT_TYPE_EXCEL -> {
                    uri = PathUtils.getDrawableUri(context, R.drawable.ic_excel_file)
                }
                BaseParam.ATTACHMENT_TYPE_WORD -> {
                    uri = PathUtils.getDrawableUri(context, R.drawable.ic_word_file)
                }
                BaseParam.ATTACHMENT_TYPE_PDF -> {
                    uri = PathUtils.getDrawableUri(context, R.drawable.ic_pdf_file)
                }
            }
            GlideApp.with(context).load(uri)
                .apply(requestOptions)
                .into(imageView)
        }
    }
}
