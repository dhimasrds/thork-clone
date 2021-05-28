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

package id.thork.app.pages.material_plan.element

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
import id.thork.app.databinding.MaterialPlanItemBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.GlideApp
import id.thork.app.pages.attachment.AttachmentActivity
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.utils.*
import timber.log.Timber


class MaterialPlanAdapter constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val requestOptions: RequestOptions,
    private val materialEntities: List<MaterialEntity>
) : RecyclerView.Adapter<MaterialPlanAdapter.MaterialPlanHolder>() {
    val TAG = MaterialPlanAdapter::class.java.name

    lateinit var materialEntity: MaterialEntity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialPlanHolder {
        val binding = DataBindingUtil.inflate<MaterialPlanItemBinding>(
            from(parent.getContext()),
            R.layout.material_plan_item, parent, false
        )
        return MaterialPlanHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialPlanHolder, position: Int) {
        val materialEntity: MaterialEntity = materialEntities[position]
        holder.bind(materialEntity)
    }

    override fun getItemCount(): Int = materialEntities.size

    inner class MaterialPlanHolder(val binding: MaterialPlanItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(materialEntity: MaterialEntity) {
            with(binding) {
                tvItemNum.text = StringUtils.truncate(materialEntity.itemNum, 30)
                tvItemType.text = StringUtils.truncate(materialEntity.itemType, 30)
                tvDescription.text = StringUtils.truncate(materialEntity.description, 30)

                root.setOnClickListener {

                }
            }
        }

    }
}