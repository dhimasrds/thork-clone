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

package id.thork.app.pages.material_plan.element.material_plan_list_item_master

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.databinding.MaterialPlanItemBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.material_plan.element.form.MaterialPlanFormActivity
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.utils.StringUtils

class MaterialPlanItemAdapter constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val requestOptions: RequestOptions,
    private val materialEntities: List<MaterialEntity>,
    private val activity: MaterialPlanItem
) : RecyclerView.Adapter<MaterialPlanItemAdapter.MaterialPlanHolder>() {
    val TAG = MaterialPlanItemAdapter::class.java.name

    lateinit var materialEntity: MaterialEntity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialPlanHolder {

        return MaterialPlanHolder(
            MaterialPlanItemBinding.inflate(
                from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MaterialPlanHolder, position: Int) {
        val materialEntity: MaterialEntity = materialEntities[position]
        holder.bind(materialEntity, activity)
    }

    override fun getItemCount(): Int = materialEntities.size

    inner class MaterialPlanHolder(val binding: MaterialPlanItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(materialEntity: MaterialEntity, activity: MaterialPlanItem) {
            with(binding) {
                tvItemNum.text = StringUtils.truncate(materialEntity.itemNum, 30)
                tvItemType.text = StringUtils.truncate(materialEntity.itemType, 30)
                tvDescription.text = StringUtils.truncate(materialEntity.description, 30)

                root.setOnClickListener {
                    val intent = Intent(BaseApplication.context, MaterialPlanFormActivity::class.java)
                    intent.putExtra(BaseParam.MATERIAL, materialEntity.itemNum)
                    intent.putExtra(BaseParam.DESCRIPTION, materialEntity.description)
                    activity.setResult(AppCompatActivity.RESULT_OK, intent)
                    activity.finish()
                }
            }
        }

    }
}