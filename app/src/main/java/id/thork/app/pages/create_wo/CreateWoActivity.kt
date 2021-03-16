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
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityCreateWorkorderBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.create_wo.element.CreateWoViewModel
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
    private lateinit var locationManager: LocationManager

    private val REQUEST_CODE_CREATE = 0
    private val TAG_CREATE = "TAG_CREATE"
    private var longDesc: String? = null

    private var selectedId = 0
    private var workPriority = 0
    private var estDur: Double? = null
    private var latitudey: Double? = null
    private var longitudex: Double? = null
    private var tempWonum: String? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@CreateWoActivity
            vm = viewModel
        }

        tempWonum = viewModel.getTempWonum()
        binding.complaintDate.setText(DateUtils.getDateTime())
        locationManager = (getSystemService(LOCATION_SERVICE) as LocationManager)
        customDialogUtils = CustomDialogUtils(this)
        dialogUtils = DialogUtils(this)

        setupToolbarWithHomeNavigation(
            getString(R.string.create_wo),
            navigation = false,
            filter = false
        )
    }

    override fun setupListener() {
        super.setupListener()

        binding.editEstimdur.setOnClickListener(setEstimdur)

        binding.pickMap.setOnClickListener {
            pickLocation()
        }

        binding.scanQr.setOnClickListener {
            gotoListMaterial()
        }

        binding.longdesc.setOnClickListener {
            gotoLongDescActivity()
        }

        binding.createWo.setOnClickListener {
            buttonCreateWo()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CREATE && resultCode == RESULT_OK) {
            longDesc = data!!.getStringExtra("longdesc")
            Timber.d("createWoLongdesc : %s", longDesc)
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
                estHour = StringUtils.convertTimeString(estHour)!!
                estMinute = StringUtils.convertTimeString(estMinute)!!
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

    private fun buttonCreateWo(){
        customDialogUtils.setTitle(R.string.information)
        customDialogUtils.setDescription(R.string.create_workorder)
        customDialogUtils.setRightButtonText(R.string.dialog_yes)
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
        customDialogUtils.setListener(this)
        customDialogUtils.show()
    }

    private fun gotoLongDescActivity(){
        val intent = Intent(this, LongDescActivity::class.java)
        intent.putExtra("TAG_CREATE", TAG_CREATE)
        intent.putExtra("TEXT_LONGDESC", longDesc)
        startActivityForResult(intent, REQUEST_CODE_CREATE)
    }

    private fun gotoListMaterial(){
        val intent = Intent(this, ListMaterialActivity::class.java)
        startActivity(intent)
    }

    private fun pickLocation(){
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
            binding.locationWo.setText(latitudey.toString() + ", " + longitudex)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onRightButton() {
        Timber.d("raka description : %s", binding.deskWo.text)
        Timber.d("raka wo priority : %s", getWorkPriority())
        Timber.d("raka estdur : %s", estDur)
        Timber.d("raka latitudey : %s", latitudey)
        Timber.d("raka longitudex : %s", longitudex)
        Timber.d("raka longdesc : %s", longDesc)

    }

    override fun onLeftButton() {
        customDialogUtils.dismiss()
    }

    override fun onMiddleButton() {
        TODO("Not yet implemented")
    }

    override fun onPositiveButton() {
        TODO("Not yet implemented")
    }

    override fun onNegativeButton() {
        TODO("Not yet implemented")
    }
}