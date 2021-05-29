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

package id.thork.app.pages.material_actual

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityMaterialActualBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.material_actual.element.MaterialActualAdapter
import id.thork.app.pages.material_actual.element.MaterialActualViewModel
import id.thork.app.pages.material_plan.element.material_plan_list.MaterialPlanItem
import id.thork.app.persistence.entity.WpmaterialEntity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MaterialActualActivity  : BaseActivity() {
    val TAG = MaterialActualActivity::class.java.name

    val viewModel: MaterialActualViewModel by viewModels()
    private val binding: ActivityMaterialActualBinding by binding(R.layout.activity_material_actual)

    private lateinit var materialActualAdapter: MaterialActualAdapter
    private lateinit var wpmaterialList: MutableList<WpmaterialEntity>

    private var intentWoId = 0

    @Inject
    @Named("svgRequestOption")
    lateinit var svgRequestOptions: RequestOptions

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun setupView() {
        super.setupView()
        wpmaterialList = mutableListOf()
        materialActualAdapter =
            MaterialActualAdapter(this, preferenceManager, svgRequestOptions, wpmaterialList)

        binding.apply {
            lifecycleOwner = this@MaterialActualActivity
            vm = viewModel

            rvMaterials.apply {
                layoutManager = LinearLayoutManager(this@MaterialActualActivity)
                addItemDecoration(
                    DividerItemDecoration(
                        this@MaterialActualActivity,
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = materialActualAdapter
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

        retrieveFromIntent()
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, MaterialPlanItem::class.java)
            startActivity(intent)
        }
    }

    private fun retrieveFromIntent() {
        intentWoId = intent.getIntExtra(BaseParam.WORKORDERID, 0)
        Timber.d("retrieveFromIntent() intentWoId: %s", intentWoId)
        viewModel.initListMaterialPlan(intentWoId.toString())
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.listMaterial.observe(this, Observer {
            wpmaterialList.clear()
            wpmaterialList.addAll(it)
            materialActualAdapter.notifyDataSetChanged()
        })
    }
}