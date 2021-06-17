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

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityMaterialPlanListBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.entity.MaterialEntity
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MaterialPlanItem : BaseActivity() {
    val TAG = MaterialPlanItem::class.java.name

    val viewModel: MaterialPlanItemViewModel by viewModels()
    private val binding: ActivityMaterialPlanListBinding by binding(R.layout.activity_material_plan_list)

    private lateinit var materialPlanItemAdapter: MaterialPlanItemAdapter
    private lateinit var materialEntities: MutableList<MaterialEntity>

    @Inject
    @Named("svgRequestOption")
    lateinit var svgRequestOptions: RequestOptions

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun setupView() {
        super.setupView()
        materialEntities = mutableListOf()
        materialPlanItemAdapter =
            MaterialPlanItemAdapter(this, preferenceManager, svgRequestOptions, materialEntities, this)

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
            option = false,
            historyAttendanceIcon = false
        )
        viewModel.initListMaterial()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.listMaterial.observe(this, Observer {
            materialEntities.clear()
            materialEntities.addAll(it)
            materialPlanItemAdapter.notifyDataSetChanged()
        })
    }
}