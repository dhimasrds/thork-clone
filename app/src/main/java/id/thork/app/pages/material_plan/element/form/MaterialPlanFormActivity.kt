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

package id.thork.app.pages.material_plan.element.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.activity.viewModels
import com.skydoves.whatif.whatIfNotNull
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityMaterialPlanBinding
import id.thork.app.databinding.ActivityMaterialPlanFormBinding
import id.thork.app.pages.material_plan.MaterialPlanActivity
import id.thork.app.pages.material_plan.element.MaterialPlanViewModel
import timber.log.Timber

@AndroidEntryPoint
class MaterialPlanFormActivity : BaseActivity() {
    val TAG = MaterialPlanFormActivity::class.java.name

    val viewModel: MaterialPlanFormViewModel by viewModels()
    private val binding: ActivityMaterialPlanFormBinding by binding(R.layout.activity_material_plan_form)

    override fun setupView() {
        super.setupView()

        binding.apply {
            lifecycleOwner = this@MaterialPlanFormActivity
            vm = viewModel
        }

        retrieveFromIntent()
    }

    private fun retrieveFromIntent() {
        val intentWoId = intent.getIntExtra(BaseParam.WORKORDERID, 0)
        Timber.d("retrieveFromIntent() intentWoId: %s", intentWoId)
    }

    fun onItemTypeClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_item ->
                    if (checked) {

                    }
                R.id.radio_material ->
                    if (checked) {

                    }
            }
        }
    }

    fun onIssueTypeCheckec(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.checkbox_direct -> {
                    if (checked) {

                    } else {

                    }
                }
            }
        }
    }


}