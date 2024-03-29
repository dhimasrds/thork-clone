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

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.google.zxing.integration.android.IntentIntegrator
import com.skydoves.whatif.whatIfNotNull
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityMaterialPlanFormBinding
import id.thork.app.helper.builder.*
import id.thork.app.helper.builder.adapter.LocomotifAdapter
import id.thork.app.helper.builder.model.LocomotifAttribute
import id.thork.app.helper.builder.widget.OnValueChangeListener
import id.thork.app.helper.builder.widget.core.LocomotifLov
import id.thork.app.helper.builder.widget.core.LocomotifLovBox
import id.thork.app.helper.builder.widget.core.LocomotifRadio
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.ScannerActivity
import id.thork.app.pages.material_plan.MaterialPlanActivity
import id.thork.app.pages.rfid_create_wo_material.RfidMaterialctivity
import id.thork.app.persistence.entity.WpmaterialEntity
import timber.log.Timber

@AndroidEntryPoint
class MaterialPlanFormActivity : BaseActivity(), LocomotifAdapter.LocomotifDialogItemClickListener,
    OnValueChangeListener, CustomDialogUtils.DialogActionListener {
    val TAG = MaterialPlanFormActivity::class.java.name

    val viewModel: MaterialPlanFormViewModel by viewModels()
    private val binding: ActivityMaterialPlanFormBinding by binding(R.layout.activity_material_plan_form)

    private var intentId:Long = 0
    private var intentState: String? = null
    private var intentWoId = BaseParam.APP_EMPTY_ID
    private var wpmaterialEntity: WpmaterialEntity? = null
    private var lineTypeItems: List<LocomotifAttribute> = listOf()
    private var storeroomItems: MutableList<LocomotifAttribute> = mutableListOf()
    private var materialItems: MutableList<LocomotifAttribute> = mutableListOf()

    private var locomotifBuilder: LocomotifBuilder<WpmaterialEntity>? = null
    private lateinit var storeroomLovBox: LocomotifLovBox
    private lateinit var storeroomLov: LocomotifLov
    private lateinit var materialLovBox: LocomotifLovBox
    private lateinit var materialLov: LocomotifLov
    private lateinit var lineType: RadioGroup
    private lateinit var direqReq: CheckBox
    private lateinit var description: AppCompatEditText
    private lateinit var itemNumGroup: View
    private lateinit var itemNumExtension: View

    private lateinit var customDialogUtils: CustomDialogUtils

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
            option = false,
            historyAttendanceIcon = false
        )

        viewModel.getLineTypeItems()
        viewModel.getStoreroomItems()
        viewModel.getMaterialItems()
        retrieveFromIntent()

        customDialogUtils = CustomDialogUtils(this)
    }

    private fun retrieveFromIntent() {
        intentState = intent.getStringExtra(BaseParam.FORM_STATE)
        intentWoId = intent.getIntExtra(BaseParam.WORKORDERID, 0)
        intentId = intent.getLongExtra(BaseParam.ID, 0)
        Timber.tag(TAG).d(
            "retrieveFromIntent() intentState: %s intentWoId: %s",
            intentState, intentWoId
        )

        intentState.whatIfNotNull { itState ->
            intentWoId.whatIfNotNull { itWoId ->
                viewModel.setupEntity(itState, itWoId, intentId)
            }
        }
    }

    private fun setupForm(wpmaterialEntity: WpmaterialEntity) {
        Timber.tag(TAG).d("setupForm() wpMaterialEntity: %s", wpmaterialEntity)
        try {
            wpmaterialEntity.whatIfNotNull {
                locomotifBuilder = LocomotifBuilder(it, this)
                locomotifBuilder?.listener = this
                locomotifBuilder?.setupFields(
                    arrayOf(
                        "lineType", "itemNum", "description", "itemQty", "storeroom", "direqReq"
                    )
                )
                locomotifBuilder?.setupFieldsCaption(
                    arrayOf(
                        "Line Type", "Item", "Description", "Qty", "Storeroom", "Direct Issue"
                    )
                )
                itemNumExtension = View.inflate(this, R.layout.material_plan_item_extension, null)
                locomotifBuilder?.setupExtensionView(itemNumExtension)
                locomotifBuilder?.forFieldItems("lineType", lineTypeItems)
                val rootView: LinearLayout = findViewById(R.id.root_view)
                rootView.removeAllViews()
                rootView.addView(locomotifBuilder?.build())

                widgetListener()
                setupFormStateAfterBuild()
            }
        } catch (exception: Exception) {
            Timber.tag(TAG).e(exception)
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
                            viewModel.getItemNumResult(material.toString())
                        }
                    }

                    BaseParam.BARCODE_REQUEST_CODE -> {
                        result.whatIfNotNull {
                            viewModel.getItemNumResult(it.contents)
                        }
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnSave.setOnClickListener {
            val isValid = locomotifBuilder?.validate()
            Timber.tag(TAG).d("setupListener() isValid: %s", isValid)
            if (isValid == true) {
                wpmaterialEntity = locomotifBuilder?.getDataAsEntity()
                wpmaterialEntity.whatIfNotNull {
                    val selectedRadioButton: RadioButton =
                        lineType.findViewById(lineType.checkedRadioButtonId)
                    it.lineType = selectedRadioButton.text.toString()
                    viewModel.saveMaterialCache(it)
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            customDialogUtils.setTitle(R.string.wo_material_plan)
            customDialogUtils.setDescription(R.string.material_plan_delete)
            customDialogUtils.setRightButtonText(R.string.dialog_yes)
            customDialogUtils.setLeftButtonText(R.string.dialog_no)
            customDialogUtils.setListener(this)
            customDialogUtils.show()
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.lineTypeItems.observe(this, {
            lineTypeItems = it
        })
        viewModel.storeroomItems.observe(this, {
            storeroomItems = it
        })
        viewModel.materialItems.observe(this, {
            materialItems = it
        })
        viewModel.materialItem.observe(this, {
            materialLovBox.value = it.itemNum
            materialLovBox.setText(it.itemNum)
            description.setText(it.description)

//            storeroomLovBox.value = ""
//            storeroomLovBox.setText("")
//            materialLov.destroy()
        })
        viewModel.wpMaterialCache.observe(this, {
            setupForm(it)

            /**
             * To trigger Radio Group Event,
             * need to reset Radio Group first
             */
            lineType.clearCheck()
            for (view in lineType.children) {
                if (view is RadioButton) {
                    if (view.text.equals(it.lineType)) {
                        lineType.check(view.id)
                    }
                }
            }
        })

        viewModel.result.observe(this, Observer {
            if (it == BaseParam.APP_TRUE) {
                gotoMaterialPlan()
            }
        })
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
        Timber.tag(TAG).d("gotoMaterialPlan() woId: %s", intentWoId)
        val intent = Intent(this, MaterialPlanActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, intentWoId)
        intent.putExtra(BaseParam.FORM_STATE, intentState)
        startActivity(intent)
        finish()
    }

    @SuppressLint("ResourceAsColor")
    private fun widgetListener() {
        storeroomLovBox = locomotifBuilder?.getWidgetByTag("storeroom") as LocomotifLovBox
        storeroomLovBox.setOnClickListener {
            val storeroomAdapter = LocomotifAdapter("storeroom", storeroomItems, this)
            storeroomLov = LocomotifLov(this, storeroomAdapter, "Storeroom")
            storeroomLov.show()
        }

        materialLovBox = locomotifBuilder?.getWidgetByTag("itemNum") as LocomotifLovBox
        materialLovBox.setOnClickListener {
            val materialAdapter = LocomotifAdapter("itemNum", materialItems, this)
            materialLov = LocomotifLov(this, materialAdapter, "Material")
            materialLov.show()
        }

        itemNumGroup = locomotifBuilder?.getWidgetGroupByTag("itemNum") as View
        lineType = locomotifBuilder?.getWidgetByTag("lineType") as LocomotifRadio
        direqReq = locomotifBuilder?.getWidgetByTag("direqReq") as CheckBox
        description = locomotifBuilder?.getWidgetByTag("description") as AppCompatEditText

        val rfidButton: AppCompatButton = itemNumExtension.findViewById(R.id.btn_rfid)
        rfidButton.setOnClickListener {
            gotoRfidMaterial()
        }

        val qrButton: AppCompatButton = itemNumExtension.findViewById(R.id.btn_qrcode)
        qrButton.setOnClickListener {
            startQRScanner(BaseParam.BARCODE_REQUEST_CODE)
        }
    }

    private fun setupFormStateAfterBuild() {
        intentState.whatIfNotNull { itState ->
            if (itState.equals(BaseParam.FORM_STATE_NEW)) {
                binding.btnDelete.visibility = View.GONE
            } else if (itState.equals(BaseParam.FORM_STATE_EDIT)) {
                binding.btnDelete.visibility = View.VISIBLE
            } else if (itState.equals(BaseParam.FORM_STATE_READ_ONLY)) {
                binding.btnDelete.visibility = View.GONE
                binding.btnSave.visibility = View.GONE
                itemNumExtension.visibility = View.GONE
                locomotifBuilder?.setFieldsReadOnly()
            }
        }
    }



    override fun clickOnItem(fieldName: String, data: LocomotifAttribute) {
        Timber.tag(TAG).d("clickOnItem() fieldName: %s", fieldName)
        when (fieldName) {
            "itemNum" -> {
                materialLovBox.setText(data.value)
                materialLovBox.value = data.value
                description.setText(data.name)

                storeroomLovBox.value = ""
                storeroomLovBox.setText("")
                materialLov.destroy()
            }
            "storeroom" -> {
                storeroomLovBox.setText(data.value)
                storeroomLovBox.value = data.value
                storeroomLov.destroy()
            }
        }
    }

    override fun onValueChange(fieldName: String, value: String) {
        Timber.tag(TAG).d("onValueChange() fieldName: %s, value: %s", fieldName, value)
        if (fieldName.equals("lineType")) {
            if (value.equals("ITEM")) {
                itemNumGroup.visibility = View.VISIBLE
                direqReq.isEnabled = true

                if (intentState.equals(BaseParam.FORM_STATE_READ_ONLY)) {
                    direqReq.isEnabled = false
                }
            } else if (value.equals("MATERIAL")) {
                itemNumGroup.visibility = View.GONE
                direqReq.isChecked = true
                direqReq.isEnabled = false

                locomotifBuilder?.forField("itemNum")?.
                isRequired(false)?.
                setValue("")
            }
        } else if (fieldName.equals("itemNum")) {
            value.whatIfNotNull {
                viewModel.getStoreroomsByItem(it)
            }
        }
    }

    /**
     * Delete Material Plan record
     */
    override fun onRightButton() {
        wpmaterialEntity = locomotifBuilder?.getDataAsEntity()
        wpmaterialEntity.whatIfNotNull {
            viewModel.deleteMaterialCache(it)
        }
        customDialogUtils.dismiss()
    }

    override fun onLeftButton() {
        customDialogUtils.dismiss()
    }

    override fun onMiddleButton() {
        customDialogUtils.dismiss()
    }
}