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

import android.content.Intent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.zxing.integration.android.IntentIntegrator
import com.skydoves.whatif.whatIfNotNull
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityMaterialPlanFormBinding
import id.thork.app.pages.ScannerActivity
import id.thork.app.pages.material_plan.MaterialPlanActivity
import id.thork.app.pages.material_plan.element.material_plan_list_item_master.MaterialPlanItem
import id.thork.app.pages.rfid_create_wo_material.RfidMaterialctivity
import id.thork.app.persistence.entity.MaterialEntity
import timber.log.Timber

@AndroidEntryPoint
class MaterialPlanFormActivity : BaseActivity() {
    val TAG = MaterialPlanFormActivity::class.java.name

    val viewModel: MaterialPlanFormViewModel by viewModels()
    private val binding: ActivityMaterialPlanFormBinding by binding(R.layout.activity_material_plan_form)
    var intentWorkorderId: String? = null
    private var materialEntity: MaterialEntity? = null

    override fun setupView() {
        super.setupView()

        binding.apply {
            lifecycleOwner = this@MaterialPlanFormActivity
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

        //TODO HARDCODE SPINNER
        var storeroom = arrayOf("STOREROOMGST")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, storeroom)
        binding.includeMaterialPlanForm.spinnerStoreroom.adapter = adapter

        retrieveFromIntent()
    }

    private fun retrieveFromIntent() {
        val intentWoId = intent.getIntExtra(BaseParam.WORKORDERID, 0)
        Timber.d("retrieveFromIntent() intentWoId: %s", intentWoId)
        intentWoId.whatIfNotNull {
            intentWorkorderId = it.toString()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(resultCode, data)

        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
                    BaseParam.RFID_REQUEST_CODE -> {
                        //TODO display decs material
                        data.whatIfNotNull {
                            val material = it.getStringExtra(BaseParam.MATERIAL)
                            val description = it.getStringExtra(BaseParam.DESCRIPTION)
                            viewModel.checkResultMaterial(material.toString())
                        }
                    }

                    BaseParam.BARCODE_REQUEST_CODE -> {
                        //TODO display decs material
                        result.whatIfNotNull {
                            viewModel.checkResultMaterial(it.contents)
                        }
                    }

                    BaseParam.REQUEST_CODE_MATERIAL_PLAN -> {
                        data.whatIfNotNull {
                            val material = it.getStringExtra(BaseParam.MATERIAL)
                            viewModel.checkResultMaterial(material.toString())
                        }
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnRfid.setOnClickListener {
            gotoRfidMaterial()
        }

        binding.btnQrcode.setOnClickListener {
            startQRScanner(BaseParam.BARCODE_REQUEST_CODE)
        }

        binding.btnSave.setOnClickListener {
            //TODO Save Record
            materialEntity.whatIfNotNull {
                viewModel.saveMaterialCache(
                    it,
                    intentWorkorderId.toString(),
                    binding.includeMaterialPlanForm.etQty.text.toString()
                )
            }
        }

        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, MaterialPlanItem::class.java)
            startActivityForResult(intent, BaseParam.REQUEST_CODE_MATERIAL_PLAN)
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.materialCache.observe(this, Observer {
            binding.etMaterial.setText(it.itemNum)
            binding.includeMaterialPlanForm.etDescription.setText(it.description)
            materialEntity = it
        })

        viewModel.result.observe(this, Observer {
            if(it == BaseParam.APP_TRUE) {
                gotoMaterialPlan()
            }
        })
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

    private fun startQRScanner(requestCode: Int) {
        IntentIntegrator(this).apply {
            setCaptureActivity(ScannerActivity::class.java)
            setRequestCode(requestCode)
            initiateScan()
        }
    }

    private fun gotoRfidMaterial() {
        val intent = Intent(this, RfidMaterialctivity::class.java)
        startActivityForResult(intent, BaseParam.RFID_REQUEST_CODE)
    }

    override fun goToPreviousActivity() {
        super.goToPreviousActivity()
        gotoMaterialPlan()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        gotoMaterialPlan()
    }

    private fun gotoMaterialPlan() {
        val intent = Intent(this, MaterialPlanActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, intentWorkorderId?.toInt())
        startActivity(intent)
        finish()
    }


}