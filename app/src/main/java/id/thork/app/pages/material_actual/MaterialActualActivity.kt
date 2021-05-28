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

import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityMaterialActualBinding
import id.thork.app.pages.material_actual.element.MaterialActualViewModel
import timber.log.Timber

class MaterialActualActivity  : BaseActivity() {
    val TAG = MaterialActualActivity::class.java.name

    val viewModel: MaterialActualViewModel by viewModels()
    private val binding: ActivityMaterialActualBinding by binding(R.layout.activity_material_actual)

    private var intentWoId = 0

    override fun setupView() {
        super.setupView()

        binding.apply {
            lifecycleOwner = this@MaterialActualActivity
            vm = viewModel
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