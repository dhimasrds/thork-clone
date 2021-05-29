package id.thork.app.pages.followup_wo

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
import androidx.lifecycle.Observer
import com.google.zxing.integration.android.IntentIntegrator
import com.skydoves.whatif.whatIfNotNull
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityFollowUpWoBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.ScannerActivity
import id.thork.app.pages.attachment.AttachmentActivity
import id.thork.app.pages.find_asset_location.FindAssetActivity
import id.thork.app.pages.find_asset_location.FindLocationActivity
import id.thork.app.pages.followup_wo.element.FollowUpWoViewModel
import id.thork.app.pages.list_material.ListMaterialActivity
import id.thork.app.pages.long_description.LongDescActivity
import id.thork.app.pages.material_plan.MaterialPlanActivity
import id.thork.app.pages.rfid_create_wo_asset.RfidCreateWoAssetActivity
import id.thork.app.pages.rfid_create_wo_location.RfidCreateWoLocationActivity
import id.thork.app.utils.DateUtils
import id.thork.app.utils.InputFilterMinMaxUtils
import id.thork.app.utils.StringUtils
import timber.log.Timber


@AndroidEntryPoint
class FollowUpWoActivity : BaseActivity(), CustomDialogUtils.DialogActionListener,
    DialogUtils.DialogUtilsListener {
    private val viewModel: FollowUpWoViewModel by viewModels()
    private val binding: ActivityFollowUpWoBinding by binding(R.layout.activity_follow_up_wo)

    private lateinit var customDialogUtils: CustomDialogUtils
    private lateinit var dialogUtils: DialogUtils
    private lateinit var radioButtonPriority: RadioButton
    private lateinit var tempWonum: String
    private var tempWorkOrderId: Int = 0

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
    private var intentWonum: String? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@FollowUpWoActivity
            vm = viewModel
        }

        tempWonum = viewModel.getTempWonum()!!
        tempWorkOrderId = viewModel.getTempWoId()!!
        binding.complaintDate.setText(DateUtils.getDateTime())
        locationManager = (getSystemService(LOCATION_SERVICE) as LocationManager)
        customDialogUtils = CustomDialogUtils(this)
        dialogUtils = DialogUtils(this)

        setupToolbarWithHomeNavigation(
            getString(R.string.followup_wo),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )
        retriveFromIntent()
    }

    private fun retriveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        Timber.d("retrieveFromIntent() %s", intentWonum)

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

        binding.attachment.setOnClickListener {
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
            if (desc.isEmpty()) {
                dialogWarning()
            } else {
                buttonCreateWo()
            }
        }

        binding.btnRfid.setOnClickListener {
            gotoFindAssetRfid()
        }

        binding.btnQrcode.setOnClickListener {
            gotoFindAssetBarcode()
        }

        binding.btnRfidLocation.setOnClickListener {
            gotoFindLocationRfid()
        }

        binding.btnQrcodeLocation.setOnClickListener {
            gotoFindLocationBarcode()
        }

        binding.includeMaterialPlan.materialPlan.setOnClickListener {
            goToMaterialPlan()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onActivityResult() requestCode %s, resultCode %s", requestCode, resultCode)
        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
                    REQUEST_CODE_CREATE -> {
                        longDesc = data!!.getStringExtra("longdesc")
                        Timber.d("createWoLongdesc : %s", longDesc)
                    }

                    REQUEST_CODE_CREATE_ASSET -> {
                        data.whatIfNotNull {
                            val asset = it.getStringExtra(BaseParam.ASSETNUM)
                            val location = it.getStringExtra(BaseParam.LOCATIONS)
                            binding.asset.text = asset
                            location.whatIfNotNull(
                                whatIf = { binding.tvLocation.text = location },
                                whatIfNot = {
                                    binding.tvLocation.text = getString(R.string.line)
                                }
                            )
                            Timber.d("onActivityResult asset : %s", asset)
                        }
                    }

                    REQUEST_CODE_CREATE_LOCATION -> {
                        data.whatIfNotNull {
                            val location = it.getStringExtra(BaseParam.LOCATIONS)
                            binding.asset.text = getString(R.string.line)
                            binding.tvLocation.text = location
                            Timber.d("onActivityResult location : %s", location)
                        }
                    }

                    BaseParam.RFID_REQUEST_CODE -> {
                        data.whatIfNotNull {
                            val assetNum = it.getStringExtra(BaseParam.ASSETNUM)
                            val location = it.getStringExtra(BaseParam.LOCATIONS)
                            binding.asset.text = assetNum
                            binding.tvLocation.text = location
                        }
                    }

                    BaseParam.BARCODE_REQUEST_CODE -> {
                        result.whatIfNotNull {
                            //TODO Query to local
                            Timber.d("onActivityResult() result scanner %s", it.contents)
                            viewModel.checkResultAsset(it.contents)
                        }
                    }

                    BaseParam.RFID_REQUEST_CODE_LOCATION -> {
                        data.whatIfNotNull {
                            val location = it.getStringExtra(BaseParam.LOCATIONS)
                            binding.tvLocation.text = location
                        }
                    }

                    BaseParam.BARCODE_REQUEST_CODE_LOCATION -> {
                        result.whatIfNotNull {
                            viewModel.checkResultLocation(it.contents)
                        }
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
        intent.putExtra(BaseParam.WORKORDERID, tempWorkOrderId)
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

    private fun gotoFindAssetRfid() {
        val intent = Intent(this, RfidCreateWoAssetActivity::class.java)
        startActivityForResult(intent, BaseParam.RFID_REQUEST_CODE)
    }

    private fun gotoFindAssetBarcode() {
        startQRScanner(BaseParam.BARCODE_REQUEST_CODE)
    }

    private fun gotoFindLocationRfid() {
        val intent = Intent(this, RfidCreateWoLocationActivity::class.java)
        startActivityForResult(intent, BaseParam.RFID_REQUEST_CODE_LOCATION)
    }

    private fun gotoFindLocationBarcode() {
        startQRScanner(BaseParam.BARCODE_REQUEST_CODE_LOCATION)
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
            binding.deskWo.text.toString(),
            estDur,
            getWorkPriority(),
            longDesc,
            tempWonum,
            binding.asset.text.toString(),
            binding.tvLocation.text.toString(),
            intentWonum.toString(),
            tempWorkOrderId.toString()
        )
        Toast.makeText(
            this,
            StringUtils.getStringResources(this, R.string.workorder_create_success),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updateWoOffline() {
        viewModel.createNewWoCache(
            binding.deskWo.text.toString(),
            estDur,
            getWorkPriority(),
            longDesc,
            binding.asset.text.toString(),
            binding.tvLocation.text.toString(),
            intentWonum.toString(),
            tempWorkOrderId.toString()
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
        val vibrator =
            BaseApplication.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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

    private fun startQRScanner(requestCode: Int) {
        IntentIntegrator(this).apply {
            setCaptureActivity(ScannerActivity::class.java)
            setRequestCode(requestCode)
            initiateScan()
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.assetCache.observe(this, Observer {
            binding.asset.text = it.assetnum
            binding.tvLocation.text = it.assetLocation
        })

        viewModel.locationCache.observe(this, Observer {
            binding.tvLocation.text = it.location
        })
    }

    override fun onResume() {
        super.onResume()
        customDialogUtils.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        customDialogUtils.dismiss()
    }

    private fun goToMaterialPlan() {
        val intent = Intent(this, MaterialPlanActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, tempWorkOrderId)
        startActivity(intent)
    }
}