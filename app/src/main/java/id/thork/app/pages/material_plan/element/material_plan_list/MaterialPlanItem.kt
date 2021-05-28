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

package id.thork.app.pages.material_plan.element.material_plan_list

import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityMaterialPlanListBinding
import id.thork.app.persistence.entity.MaterialEntity

@AndroidEntryPoint
class MaterialPlanItem : BaseActivity() {
    val TAG = MaterialPlanItem::class.java.name

    val viewModel: MaterialPlanItemViewModel by viewModels()
    private val binding: ActivityMaterialPlanListBinding by binding(R.layout.activity_material_plan_list)

    private lateinit var materialPlanItemAdapter: MaterialPlanItemAdapter
    private lateinit var materialEntities: MutableList<MaterialEntity>

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@MaterialPlanItem
            vm = viewModel
            rvMaterials.apply {
                layoutManager = LinearLayoutManager(this@MaterialPlanItem)
                addItemDecoration(
                    DividerItemDecoration(
                        this@MaterialPlanItem,
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = materialPlanItemAdapter
            }
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.wo_material_plan),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )
    }
}