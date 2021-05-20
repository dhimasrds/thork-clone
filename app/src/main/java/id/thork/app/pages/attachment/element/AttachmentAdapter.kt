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
import android.content.Intent
import android.content.res.AssetManager
import android.net.Uri
import android.view.LayoutInflater.from
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import id.thork.app.BuildConfig
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.databinding.AttachmentItemBinding
import id.thork.app.network.GlideApp
import id.thork.app.pages.attachment.AttachmentActivity
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.utils.PathUtils
import id.thork.app.utils.StringUtils
import timber.log.Timber
import java.io.File


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
                classifyImageThumbnail(attachmentEntity, ivThumbnail)

                root.setOnClickListener {
                    val fileName: String = "filepdf.pdf"
                    val newFile = File(context.getExternalFilesDir(null)?.absolutePath + "/" + fileName)
                    val fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID
                    + ".provider", newFile)
                    context.grantUriPermission(context.packageName, fileUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(fileUri, "application/pdf")
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.startActivity(intent)
                }
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
