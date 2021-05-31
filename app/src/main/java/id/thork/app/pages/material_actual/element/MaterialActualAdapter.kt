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

package id.thork.app.pages.material_actual.element

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import id.thork.app.R
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.databinding.MaterialPlanItemBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.material_actual.MaterialActualActivity
import id.thork.app.pages.material_actual.element.detail_material_actual.MaterialActualDetail
import id.thork.app.persistence.entity.MatusetransEntity
import id.thork.app.utils.StringUtils
import java.util.*


class MaterialActualAdapter constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val requestOptions: RequestOptions,
    private val matusetransEntityList: List<MatusetransEntity>,
    private val activity: MaterialActualActivity
) : RecyclerView.Adapter<MaterialActualAdapter.MaterialPlanHolder>(),
    CustomDialogUtils.DialogActionListener {
    val TAG = MaterialActualAdapter::class.java.name

    lateinit var matusetransEntity: MatusetransEntity
    private lateinit var customDialogUtils: CustomDialogUtils
    var currentItemnum : String? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialPlanHolder {
        val binding = DataBindingUtil.inflate<MaterialPlanItemBinding>(
            from(parent.getContext()),
            R.layout.material_plan_item, parent, false
        )
        return MaterialPlanHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialPlanHolder, position: Int) {
        val matusetransEntity: MatusetransEntity = matusetransEntityList[position]
        holder.bind(matusetransEntity, activity)
    }

    override fun getItemCount(): Int = matusetransEntityList.size

    inner class MaterialPlanHolder(val binding: MaterialPlanItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(matusetransEntity: MatusetransEntity, activity: MaterialActualActivity) {
            with(binding) {

                tvItemNum.text = StringUtils.truncate(matusetransEntity.itemNum, 30)
                tvItemType.text = StringUtils.truncate(matusetransEntity.itemType, 30)
                tvDescription.text = StringUtils.truncate(matusetransEntity.description, 30)
                ivDelete.visibility = View.VISIBLE
                ivDelete.setOnClickListener {
                    customDialogUtils = CustomDialogUtils(context)
                    customDialogUtils.setTitle(R.string.information)
                    customDialogUtils.setDescription(R.string.material_actual_dialog)
                    customDialogUtils.setRightButtonText(R.string.dialog_yes)
                    customDialogUtils.setLeftButtonText(R.string.dialog_no)
                    customDialogUtils.setListener(this@MaterialActualAdapter)
                    customDialogUtils.show()
                    currentItemnum = matusetransEntity.itemNum
                }

                root.setOnClickListener {
                    val intent =
                        Intent(BaseApplication.context, MaterialActualDetail::class.java)
                    intent.putExtra(BaseParam.WORKORDERID, matusetransEntity.workorderId)
                    intent.putExtra(BaseParam.MATERIAL, matusetransEntity.itemNum)
                    activity.startActivity(intent)
                    activity.finish()

                }
            }
        }

    }

    override fun onRightButton() {
        activity.deleteMaterial(currentItemnum)
        customDialogUtils.dismiss()
    }

    override fun onLeftButton() {
        customDialogUtils.dismiss()
    }

    override fun onMiddleButton() {
        TODO("Not yet implemented")
    }
}