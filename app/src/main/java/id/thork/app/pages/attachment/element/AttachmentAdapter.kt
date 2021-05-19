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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.pages.attachment.AttachmentActivity
import id.thork.app.persistence.entity.AttachmentEntity

class AttachmentAdapter constructor(
    private val context: Context,
    private val attachmentActivity: AttachmentActivity,
    private val attachmentEntities: List<AttachmentEntity>
) : RecyclerView.Adapter<AttachmentAdapter.AttachmentHolder>() {

    lateinit var attachmentEntity: AttachmentEntity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentHolder {
        val layoutInflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val adapterView = layoutInflater.inflate(R.layout.attachment_item, parent, false)
        return AttachmentHolder(adapterView)
    }

    override fun onBindViewHolder(holder: AttachmentHolder, position: Int) {
        val attachmentEntity: AttachmentEntity = attachmentEntities[position]
        holder.bind(attachmentEntity)
    }

    override fun getItemCount(): Int = attachmentEntities.size


    class AttachmentHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var tvFilename: TextView? = null

        init {
            tvFilename = view.findViewById(R.id.tv_attachment_filename)
        }

        fun bind(attachmentEntity: AttachmentEntity) {
            tvFilename?.text = attachmentEntity.name
        }
    }
}
