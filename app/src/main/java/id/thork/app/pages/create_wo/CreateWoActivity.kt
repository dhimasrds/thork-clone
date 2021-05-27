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

package id.thork.app.pages.create_wo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputFilter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import com.skydoves.whatif.whatIf
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseApplication.Constants.context
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityCreateWorkorderBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.attachment.AttachmentActivity
import id.thork.app.pages.create_wo.element.CreateWoViewModel
import id.thork.app.pages.find_asset_location.FindAssetActivity
import id.thork.app.pages.find_asset_location.FindLocationActivity
import id.thork.app.pages.list_material.ListMaterialActivity
import id.thork.app.pages.long_description.LongDescActivity
import id.thork.app.utils.DateUtils
import id.thork.app.utils.InputFilterMinMaxUtils
import id.thork.app.utils.StringUtils
import timber.log.Timber

class CreateWoActivity : BaseActivity(), CustomDialogUtils.DialogActionListener,
    DialogUtils.DialogUtilsListener {
    private val viewModel: CreateWoViewModel by viewModels()
    private val binding: ActivityCreateWorkorderBinding by binding(R.layout.activity_create_workorder)

    private lateinit var customDialogUtils: CustomDialogUtils
    private lateinit var dialogUtils: DialogUtils
    private lateinit var radioButtonPriority: RadioButton
    private lateinit var tempWonum: String

    private lateinit var locationManager: LocationManager
    private val REQUEST_CODE_CREATE = 0
    private val REQUEST_CODE_CREATE_ASSET = 10
    private val REQUEST_CODE_CREATE_LOCATION = 20
    private val TAG_CREATE = "TAG_CREATE"

    private var longDesc: String? = null
    private var selectedId = 0
    private var workPriority = 0
    private var estDur: Double? = null
    private var latitudey: Double? = null
    private var longitudex: Double? = null
    private var validateDialogExit: Boolean = false

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@CreateWoActivity
            vm = viewModel
        }

        tempWonum = viewModel.getTempWonum()!!
        binding.complaintDate.setText(DateUtils.getDateTime())
        locationManager = (getSystemService(LOCATION_SERVICE) as LocationManager)
        customDialogUtils = CustomDialogUtils(this)
        dialogUtils = DialogUtils(this)

        setupToolbarWithHomeNavigation(
            getString(R.string.create_wo),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )
    }

    override fun setupListener() {
        super.setupListener()

        binding.editEstimdur.setOnClickListener(setEstimdur)

        binding.pickMap.setOnClickListener {
            vibrateAndPickLocation()
        }

        binding.scanQr.setOnClickListener {
            gotoListMaterial()
        }

        binding.longdesc.setOnClickListener {
            gotoLongDescActivity()
        }

        binding.takePhoto.setOnClickListener {
            goToAttachments()
        }

        binding.findAsset.setOnClickListener {
            gotoFindAsset()
        }

        binding.findLocation.setOnClickListener {
            gotoFindLocation()
        }

        binding.createWo.setOnClickListener {
            val desc: String = binding.deskWo.text.toString()
            if (desc.isEmpty() || latitudey == null || longitudex == null) {
                dialogWarning()
            } else {
                buttonCreateWo()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onActivityResult() requestCode %s, resultCode %s", requestCode, resultCode)

        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
                    REQUEST_CODE_CREATE -> {
                        longDesc = data!!.getStringExtra("longdesc")
                        Timber.d("createWoLongdesc : %s", longDesc)
                    }
                    REQUEST_CODE_CREATE_ASSET -> {
                        val asset = data!!.getStringExtra(BaseParam.ASSETNUM)
                        val location = data!!.getStringExtra(BaseParam.LOCATIONS)
                        binding.asset.text = asset
                        location.whatIfNotNull(
                            whatIf = { binding.tvLocation.text = location },
                            whatIfNot = {
                                binding.tvLocation.text = getString(R.string.line)
                            }
                        )
                        Timber.d("onActivityResult asset : %s", asset)
                    }
                    REQUEST_CODE_CREATE_LOCATION -> {
                        val location = data!!.getStringExtra(BaseParam.LOCATIONS)
                        binding.asset.text = getString(R.string.line)
                        binding.tvLocation.text = location
                        Timber.d("onActivityResult location : %s", location)
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("SetTextI18n")
    var setEstimdur = View.OnClickListener {
        dialogUtils.setInflater(R.layout.dialog_estdur, null, layoutInflater).create().setRounded(
            true
        )
        dialogUtils.show()
        val esdurHours = dialogUtils.setViewId(R.id.esdur_hours) as EditText
        val esdurMinutes = dialogUtils.setViewId(R.id.esdur_minutes) as EditText
        esdurHours.filters = arrayOf<InputFilter>(InputFilterMinMaxUtils(0, 24))
        esdurMinutes.filters = arrayOf<InputFilter>(InputFilterMinMaxUtils(0, 60))
        val saveEst = dialogUtils.setViewId(R.id.save_est) as Button
        saveEst.setOnClickListener {
            if (esdurHours.text.toString().isEmpty() && esdurMinutes.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.estimatedhour_estimatedminute,
                    Toast.LENGTH_LONG
                ).show()
            } else if (esdurHours.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.estimatedhour,
                    Toast.LENGTH_LONG
                ).show()
            } else if (esdurMinutes.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.estimatedminute,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val estH: Int = esdurHours.text.toString().toInt()
                val estM: Int = esdurMinutes.text.toString().toInt()
                var estHour = estH.toString()
                var estMinute = estM.toString()
                estHour = StringUtils.convertTimeString(estHour)
                estMinute = StringUtils.convertTimeString(estMinute)
                binding.editEstimdur.text = "$estHour " + StringUtils.getStringResources(
                    this,
                    R.string.estHour
                ) + " : " + estMinute + " " + StringUtils.getStringResources(
                    this,
                    R.string.estMinute
                )
                estDur = viewModel.estDuration(estH, estM)
                dialogUtils.dismiss()
            }
        }
    }

    private fun getWorkPriority(): Int {
        selectedId = binding.radioGroupPriority.checkedRadioButtonId
        radioButtonPriority = findViewById(selectedId)
        if (radioButtonPriority.text != null) {
            when (radioButtonPriority.text) {
                BaseParam.PRIORITY_NORMAL_DESC -> {
                    1.also { workPriority = it }
                }
                BaseParam.PRIORITY_MEDIUM_DESC -> {
                    2.also { workPriority = it }
                }
                BaseParam.PRIORITY_HIGH_DESC -> {
                    3.also { workPriority = it }
                }
                else -> {
                    return workPriority
                }
            }
        }
        return workPriority
    }

    private fun gotoLongDescActivity() {
        val intent = Intent(this, LongDescActivity::class.java)
        intent.putExtra("TAG_CREATE", TAG_CREATE)
        intent.putExtra("TEXT_LONGDESC", longDesc)
        startActivityForResult(intent, REQUEST_CODE_CREATE)
    }

    private fun gotoListMaterial() {
        val intent = Intent(this, ListMaterialActivity::class.java)
        intent.putExtra(BaseParam.WONUM, tempWonum)
        startActivity(intent)
    }

    private fun goToAttachments() {
        val intent = Intent(this, AttachmentActivity::class.java)
        startActivity(intent)
    }

    private fun gotoFindAsset() {
        val intent = Intent(this, FindAssetActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_CREATE_ASSET)
    }

    private fun gotoFindLocation() {
        val intent = Intent(this, FindLocationActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_CREATE_LOCATION)
    }

    private fun pickLocation() {
        try {
            // Request location updates
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                locationListener
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            latitudey = location.latitude
            longitudex = location.longitude
            binding.locationWo.setText("$latitudey , $longitudex")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onRightButton() {
        if (validateDialogExit) {
            viewModel.removeScanner(tempWonum)
        } else {
            if (isConnected) {
                updateWoOnline()
            } else {
                updateWoOffline()
            }
            Timber.d(
                "createNewWo() desc:%s, long:%s, lat:%s, estDur:%s, workPriority:%s, longdesc:%s",
                binding.deskWo.text.toString(),
                longitudex,
                latitudey,
                estDur,
                workPriority,
                longDesc
            )
        }
        gotoHome()
    }

    private fun updateWoOnline() {
        viewModel.createWorkOrderOnline(
            binding.deskWo.text.toString(), longitudex,
            latitudey, estDur, getWorkPriority(), longDesc, tempWonum
        )
        Toast.makeText(
            this,
            StringUtils.getStringResources(this, R.string.workorder_create_success),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updateWoOffline() {
        viewModel.createNewWoCache(
            longitudex,
            latitudey,
            binding.deskWo.text.toString(),
            estDur,
            getWorkPriority(),
            longDesc
        )
        Toast.makeText(
            this,
            StringUtils.getStringResources(this, R.string.workorder_create_failed),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onLeftButton() {
        if (validateDialogExit) {
            validateDialogExit = false
        }
        customDialogUtils.dismiss()
    }

    override fun onMiddleButton() {
        customDialogUtils.dismiss()
    }

    override fun onPositiveButton() {
        customDialogUtils.dismiss()
    }

    override fun onNegativeButton() {
        customDialogUtils.dismiss()
    }

    override fun onBackPressed() {
        dialogExit()
    }

    override fun goToPreviousActivity() {
        dialogExit()
    }

    private fun buttonCreateWo() {
        customDialogUtils.setTitle(R.string.information)
        customDialogUtils.setDescription(R.string.create_workorder)
        customDialogUtils.setRightButtonText(R.string.dialog_yes)
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
        customDialogUtils.setListener(this)
        customDialogUtils.show()
    }

    private fun dialogExit() {
        validateDialogExit = true
        customDialogUtils.setTitle(R.string.service_request_create)
        customDialogUtils.setDescription(R.string.service_request_cancel)
        customDialogUtils.setRightButtonText(R.string.dialog_yes)
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
        customDialogUtils.setListener(this)
        customDialogUtils.show()
    }

    private fun dialogWarning() {
        customDialogUtils.setTitle(R.string.warning)
        customDialogUtils.setDescription(R.string.warning_workorder)
        customDialogUtils.setMiddleButtonText(R.string.dialog_yes)
        customDialogUtils.setListener(this)
        customDialogUtils.show()
    }

    @Suppress("DEPRECATION")
    private fun vibrateAndPickLocation() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    100,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(100)
        }
        pickLocation()
    }

    private fun gotoHome() {
        finish()
    }
}