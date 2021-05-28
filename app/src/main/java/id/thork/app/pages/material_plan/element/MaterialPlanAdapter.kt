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
import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import id.thork.app.R
import id.thork.app.databinding.MaterialPlanItemBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.entity.MatTransEntity
import id.thork.app.utils.StringUtils


class MaterialPlanAdapter constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val requestOptions: RequestOptions,
    private val materialEntities: List<MatTransEntity>
) : RecyclerView.Adapter<MaterialPlanAdapter.MaterialPlanHolder>() {
    val TAG = MaterialPlanAdapter::class.java.name

    lateinit var matTransEntity: MatTransEntity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialPlanHolder {
        val binding = DataBindingUtil.inflate<MaterialPlanItemBinding>(
            from(parent.getContext()),
            R.layout.material_plan_item, parent, false
        )
        return MaterialPlanHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialPlanHolder, position: Int) {
        val matTransEntity: MatTransEntity = materialEntities[position]
        holder.bind(matTransEntity)
    }

    override fun getItemCount(): Int = materialEntities.size

    inner class MaterialPlanHolder(val binding: MaterialPlanItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(matTransEntity: MatTransEntity) {
            with(binding) {
                tvItemNum.text = StringUtils.truncate(matTransEntity.itemNum, 30)
                tvItemType.text = StringUtils.truncate(matTransEntity.itemType, 30)
                tvDescription.text = StringUtils.truncate(matTransEntity.description, 30)

                root.setOnClickListener {

                }
            }
        }

    }
}