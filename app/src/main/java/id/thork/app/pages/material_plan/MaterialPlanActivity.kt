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

package id.thork.app.pages.material_plan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityAttachmentBinding
import id.thork.app.databinding.ActivityMaterialPlanBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.attachment.AttachmentActivity
import id.thork.app.pages.attachment.element.AttachmentAdapter
import id.thork.app.pages.attachment.element.AttachmentViewModel
import id.thork.app.pages.material_plan.element.MaterialPlanAdapter
import id.thork.app.pages.material_plan.element.MaterialPlanViewModel
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.persistence.entity.MaterialEntity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MaterialPlanActivity : BaseActivity() {
    val TAG = MaterialPlanActivity::class.java.name

    val viewModel: MaterialPlanViewModel by viewModels()
    private val binding: ActivityMaterialPlanBinding by binding(R.layout.activity_material_plan)

    private lateinit var materialPlanAdapter: MaterialPlanAdapter
    private lateinit var materialEntities: MutableList<MaterialEntity>

    private var intentWoId = 0

    @Inject
    @Named("svgRequestOption")
    lateinit var svgRequestOptions: RequestOptions

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun setupView() {
        super.setupView()

        materialPlanAdapter =
            MaterialPlanAdapter(this, preferenceManager, svgRequestOptions, materialEntities)

        binding.apply {
            lifecycleOwner = this@MaterialPlanActivity
            vm = viewModel

            rvMaterials.apply {
                layoutManager = LinearLayoutManager(this@MaterialPlanActivity)
                addItemDecoration(
                    DividerItemDecoration(
                        this@MaterialPlanActivity,
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = materialPlanAdapter
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

    private fun retrieveFromIntent() {
        intentWoId = intent.getIntExtra(BaseParam.WORKORDERID, 0)
        Timber.d("retrieveFromIntent() intentWoId: %s", intentWoId)
    }
}