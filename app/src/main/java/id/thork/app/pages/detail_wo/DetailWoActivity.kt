package id.thork.app.pages.detail_wo

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.zxing.integration.android.IntentIntegrator
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityDetailWoBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.ScannerActivity
import id.thork.app.pages.attachment.AttachmentActivity
import id.thork.app.pages.detail_wo.element.DetailWoViewModel
import id.thork.app.pages.labor_actual.LaborActualActivity
import id.thork.app.pages.labor_plan.LaborPlanActivity
import id.thork.app.pages.list_material.ListMaterialActivity
import id.thork.app.pages.long_description.LongDescActivity
import id.thork.app.pages.main.MainActivity
import id.thork.app.pages.material_actual.MaterialActualActivity
import id.thork.app.pages.material_plan.MaterialPlanActivity
import id.thork.app.pages.multi_asset.ListAssetActivity
import id.thork.app.pages.rfid_asset.RfidAssetAcitivty
import id.thork.app.pages.rfid_location.RfidLocationActivity
import id.thork.app.pages.task.TaskActivity
import id.thork.app.pages.work_log.WorkLogActivity
import id.thork.app.utils.CommonUtils
import id.thork.app.utils.DateUtils
import id.thork.app.utils.MapsUtils
import id.thork.app.utils.StringUtils
import timber.log.Timber

class DetailWoActivity : BaseActivity(), OnMapReadyCallback,
    CustomDialogUtils.DialogActionListener {
    private val TAG = DetailWoActivity::class.java.name
    private val REQUEST_CODE_DETAIL = 0

    private val detailWoViewModel: DetailWoViewModel by viewModels()
    private val binding: ActivityDetailWoBinding by binding(R.layout.activity_detail_wo)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var customDialogUtils: CustomDialogUtils
    private lateinit var map: GoogleMap
    private var lastKnownLocation: Location? = null
    private var intentWonum: String? = null
    private var statuswo: String? = null
    private var destinationString: String? = null
    private var destinationLatLng: LatLng? = null
    private var isRoute: Int = BaseParam.APP_FALSE
    private var workorderId: Int? = null
    private var workorderStatus: String? = null
    private var workorderNumber: String? = null
    private var workorderLongdesc: String? = null
    private var valueLongDesc: String? = null


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@DetailWoActivity
            vm = detailWoViewModel
            transparentImage.setRootView(binding.mainScrollview)
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        customDialogUtils = CustomDialogUtils(this)

        setupToolbarWithHomeNavigation(
            getString(R.string.wo_detail),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )
        retrieveFromIntent()
        setupViewCloseStatus()

        Timber.d("status detail :%s", statuswo)
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun setupObserver() {

        super.setupObserver()
        detailWoViewModel.CurrentMember.observe(this, {
            binding.apply {
                wonum.text = it.wonum
                description.text = it.description
                status.text = it.status


                it.assetnum.whatIfNotNull(
                    whatIf = { resultAsset ->
                        asset.text = StringUtils.truncate(resultAsset, 20)
                    },
                    whatIfNot = {
                        asset.text = getString(R.string.rfid_asset_empty)
                        asset.setTextColor(
                            ContextCompat.getColor(
                                this@DetailWoActivity,
                                R.color.colorRed
                            )
                        )
                        btnRfid.visibility = GONE
                        btnQrcode.visibility = GONE
                        btnQrcodeEmpty.visibility = VISIBLE
                        btnRfidAssetEmpty.visibility = VISIBLE
                    }
                )
                it.location.whatIfNotNull(
                    whatIf = { resultLocation ->
                        location.text = StringUtils.truncate(resultLocation, 20)
                    },
                    whatIfNot = {
                        location.text = getString(R.string.rfid_location_empty)
                        location.setTextColor(
                            ContextCompat.getColor(
                                this@DetailWoActivity,
                                R.color.colorRed
                            )
                        )
                        btnRfidLocation.visibility = GONE
                        btnQrcodeLocation.visibility = GONE
                        btnQrcodeLocationEmpty.visibility = VISIBLE
                        btnRfidLocationEmpty.visibility = VISIBLE
                    }
                )
                it.woserviceaddress.whatIfNotNull { address ->
                    serviceaddress.text = address.get(0).formattedaddress
                }
                estimatedDuration.text = StringUtils.NVL(
                    java.lang.String.valueOf(it.estdur),
                    BaseParam.APP_DASH
                )
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    reportDate.text = StringUtils.NVL(
                        DateUtils.convertDateFormat(it.reportdate),
                        BaseParam.APP_DASH
                    )
                }

                workorderId = it.workorderid
                it.wonum.whatIfNotNull {
                    workorderNumber = it
                }
                workorderStatus = it.status
                workorderLongdesc = it.descriptionLongdescription
                it.status?.let { status -> setButtonStatus(status) }
            }
            if (it.woserviceaddress?.get(0)?.latitudey != null && it.woserviceaddress[0].longitudex != null) {
                isRoute = BaseParam.APP_TRUE
                destinationLatLng = LatLng(
                    it.woserviceaddress[0].latitudey!!,
                    it.woserviceaddress[0].longitudex!!
                )
                destinationString =
                    "${it.woserviceaddress[0].latitudey!!},${it.woserviceaddress[0].longitudex!!}"
            }
        })

        detailWoViewModel.RequestRoute.observe(this, {
            val deviceLocation = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            MapsUtils.renderMapDirection(map, this, it, deviceLocation, destinationLatLng)
        })

        detailWoViewModel.MapsInfo.observe(this, {
            binding.apply {
                if (it != null) {
                    tvDistance.text = it.distanceText
                    tvDuration.text = it.durationText
                }
            }
        })

        detailWoViewModel.Result.observe(this, {
            if (it.equals(BaseParam.APP_TRUE)) {
//            assetIsMatch = result
                binding.icCheckAsset.visibility = View.VISIBLE
                binding.icCrossAsset.visibility = GONE
                binding.tvScanResultAsset.text =
                    getString(R.string.asset_scan_result) + ": " +
                            getString(R.string.asset_rfid_is_match_begin) + " " + getString(R.string.asset_rfid_is_match)
                binding.tvScanResultAsset.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext, R.color.colorGreen
                    )
                )
            } else {
                binding.icCheckAsset.visibility = GONE
                binding.icCrossAsset.visibility = View.VISIBLE
                binding.tvScanResultAsset.text = (getString(R.string.asset_scan_result) + ": " +
                        getString(R.string.asset_rfid_is_not_match))
                binding.tvScanResultAsset.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext, R.color.colorRed
                    )
                )
            }
        })

        //TODO Result Query Asset for Rfid
        detailWoViewModel.AssetRfid.observe(this, {
            if (it == BaseParam.APP_DASH) {
                CommonUtils.standardToast(getString(R.string.asset_rfid_is_not_registered))
            } else {
                gotoRfidAsset(it)
            }
        })

        detailWoViewModel.ResultLocation.observe(this, {
            if (it.equals(BaseParam.APP_TRUE)) {
//            locationIsMatch = result
                binding.icCheckLocation.visibility = VISIBLE
                binding.icCrossLocation.visibility = GONE
                binding.tvScanResultLocation.text =
                    getString(R.string.asset_scan_result) + ": " +
                            getString(R.string.location_rfid_is_match_begin) + " " + getString(R.string.asset_rfid_is_match)
                binding.tvScanResultLocation.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext, R.color.colorGreen
                    )
                )
            } else {
                binding.icCheckLocation.visibility = GONE
                binding.icCrossLocation.visibility = VISIBLE
                binding.tvScanResultLocation.text = (getString(R.string.asset_scan_result) + ": " +
                        getString(R.string.location_rfid_is_not_match))
                binding.tvScanResultLocation.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext, R.color.colorRed
                    )
                )
            }
        })

        //TODO Result Query Location for Rfid
        detailWoViewModel.LocationRfid.observe(this, {
            Timber.d("Observer Location Rfid() %s", it)
            if (it == BaseParam.APP_DASH) {
                CommonUtils.standardToast(getString(R.string.location_rfid_is_not_registered))
            } else {
                gotoRfidLocation(it)
            }
        })
    }

    private fun retrieveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.APP_WONUM)
        statuswo = intent.getStringExtra(BaseParam.STATUS)
        intentWonum.whatIfNotNull {
            detailWoViewModel.fetchWobyWonum(it)
            enableFollowUpWo(true, it)
        }
        val intentPriority = intent.getIntExtra(BaseParam.PRIORITY, 0)
        detailWoViewModel.setPriority(intentPriority)
    }


    private fun setupViewCloseStatus() {
        binding.apply {
            if (statuswo.equals(BaseParam.CLOSED)) {
                Timber.d("setupViewCloseStatus status detail :%s", statuswo)
                btnRfid.isEnabled = false
                btnQrcode.isEnabled = false
                btnRfidLocation.isEnabled = false
                btnQrcodeLocation.isEnabled = false
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            map = googleMap
        }
        getDeviceLocation()
    }

    private fun updateLocationUI() {
        try {
            with(map) {
                isMyLocationEnabled = true
                uiSettings.isMyLocationButtonEnabled = true
            }
        } catch (e: SecurityException) {
            Timber.tag(TAG).d("updateLocationUI() catch: %s", e.toString())
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener {
                if (it.isSuccessful) {
                    lastKnownLocation = it.result
                    if (lastKnownLocation != null) {
                        if (isRoute == BaseParam.APP_TRUE) {
                            val originString =
                                "${lastKnownLocation!!.latitude},${lastKnownLocation!!.longitude}"
                            detailWoViewModel.requestRoute(originString, destinationString!!)
                        } else {
                            updateLocationUI()
                            MapsUtils.renderCurrentLocation(map, lastKnownLocation)
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Timber.tag(TAG).d("getDeviceLocation(): %s", e.message)
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.includeAttachments.attachment.setOnClickListener {
            goToAttachments()
        }

        binding.btnRfid.setOnClickListener {
            //TODO Need validation after Query to local
            detailWoViewModel.validateAsset(binding.asset.text.toString())
        }

        binding.btnQrcode.setOnClickListener {
            startQRScanner(BaseParam.BARCODE_REQUEST_CODE)
        }

        binding.btnRfidLocation.setOnClickListener {
            detailWoViewModel.validateLocation(binding.location.text.toString())
        }

        binding.btnQrcodeLocation.setOnClickListener {
            startQRScanner(BaseParam.BARCODE_REQUEST_CODE_LOCATION)
        }

        binding.includeTask.cardTask.setOnClickListener {
            gotoTaskActivity()
        }

        binding.includeLaborplan.laborPlan.setOnClickListener {
            goToLaborPlan()
        }

        binding.includeLaboractual.laborActual.setOnClickListener {
            goToLaborActual()
        }
    }

    private fun gotoListMaterial() {
        val intent = Intent(this, ListMaterialActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, workorderId)
        intent.putExtra(BaseParam.STATUS, workorderStatus)
        startActivity(intent)
    }


    private fun gotoLongDescription() {
        val intent = Intent(this, LongDescActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, workorderId)
        intent.putExtra(BaseParam.STATUS, workorderStatus)
        intent.putExtra(BaseParam.LONGDESC, workorderLongdesc)
        intent.putExtra(BaseParam.WONUM, workorderNumber)
        startActivityForResult(intent, REQUEST_CODE_DETAIL)
    }

    private fun gotoListAsset() {
        val intent = Intent(this, ListAssetActivity::class.java)
        intent.putExtra(BaseParam.WONUM, workorderNumber)
        startActivity(intent)
    }

    private fun goToAttachments() {
        val intent = Intent(this, AttachmentActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, workorderId)
        intent.putExtra(BaseParam.STATUS, statuswo)
        startActivity(intent)
    }

    private fun goToMaterialPlan() {
        val intent = Intent(this, MaterialPlanActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, workorderId)
        intent.putExtra(BaseParam.FORM_STATE, BaseParam.FORM_STATE_READ_ONLY)
        startActivity(intent)
    }

    private fun goToWorkLog() {
        val intent = Intent(this, WorkLogActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, workorderId.toString())
        intent.putExtra(BaseParam.WONUM, workorderNumber.toString())
        intent.putExtra(BaseParam.STATUS, statuswo)
        startActivity(intent)
    }

    private fun gotoTaskActivity() {
        val intent = Intent(this, TaskActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, workorderId)
        intent.putExtra(BaseParam.WONUM, workorderNumber)
        intent.putExtra(BaseParam.STATUS, workorderStatus)
        startActivity(intent)
    }

    private fun goToMaterialActual() {
        val intent = Intent(this, MaterialActualActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, workorderId)
        intent.putExtra(BaseParam.STATUS, statuswo)
        intent.putExtra(BaseParam.FORM_STATE, BaseParam.FORM_STATE_EDIT)
        startActivity(intent)
    }

    //TODO navigate to Rfid Asset
    private fun gotoRfidAsset(assetnum: String) {
        val intent = Intent(this, RfidAssetAcitivty::class.java)
        intent.putExtra(BaseParam.RFID_ASSETNUM, assetnum)
        startActivityForResult(intent, BaseParam.RFID_REQUEST_CODE)
    }

    //TODO navigate to Rfid Location
    private fun gotoRfidLocation(location: String) {
        val intentLocation = Intent(this, RfidLocationActivity::class.java)
        intentLocation.putExtra(BaseParam.RFID_LOCATION, location)
        startActivityForResult(intentLocation, BaseParam.RFID_REQUEST_CODE_LOCATION)
    }

    private fun goToLaborPlan() {
        val intent = Intent(this, LaborPlanActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, workorderId)
        intent.putExtra(BaseParam.WONUM, workorderNumber)
        intent.putExtra(BaseParam.STATUS, workorderStatus)
        startActivity(intent)
    }

    private fun goToLaborActual() {
        val intent = Intent(this, LaborActualActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, workorderId)
        intent.putExtra(BaseParam.WONUM, workorderNumber)
        intent.putExtra(BaseParam.STATUS, workorderStatus)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //TODO Nested when
        val result = IntentIntegrator.parseActivityResult(resultCode, data)

        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
                    REQUEST_CODE_DETAIL -> {
                        valueLongDesc = data!!.getStringExtra("longdesc")
                        workorderLongdesc = valueLongDesc
                    }

                    BaseParam.RFID_REQUEST_CODE -> {
                        data.whatIfNotNull {
                            val assetIsMatch =
                                it.getBooleanExtra(BaseParam.RFID_ASSET_IS_MATCH, false)
                            detailWoViewModel.checkingResultAsset(assetIsMatch)
                        }
                    }

                    BaseParam.RFID_REQUEST_CODE_LOCATION -> {
                        data.whatIfNotNull {
                            val locationIsMatch =
                                it.getBooleanExtra(BaseParam.RFID_LOCATION_IS_MATCH, false)
                            detailWoViewModel.checkingResultLocation(locationIsMatch)
                        }
                    }

                    BaseParam.BARCODE_REQUEST_CODE -> {
                        result.whatIfNotNull {
                            val resultCompareAsset = detailWoViewModel.compareResultScanner(
                                binding.asset.text.toString(),
                                it.contents
                            )
                            detailWoViewModel.checkingResultAsset(resultCompareAsset)
                        }
                    }

                    BaseParam.BARCODE_REQUEST_CODE_LOCATION -> {
                        result.whatIfNotNull {
                            val resultCompareLocation = detailWoViewModel.compareResultScanner(
                                binding.location.text.toString(),
                                it.contents
                            )
                            detailWoViewModel.checkingResultLocation(resultCompareLocation)
                        }
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setButtonStatus(workorderStatus: String) {
        when (workorderStatus) {
            BaseParam.APPROVED -> {
                hideMore()
                binding.apply {
                    textStatus.text = BaseParam.INPROGRESS
                    status.setTextColor(
                        ContextCompat.getColor(
                            this@DetailWoActivity,
                            R.color.colorBlueDispatch
                        )
                    )
                    bgStatus.background = ContextCompat.getDrawable(
                        this@DetailWoActivity,
                        R.drawable.bg_status_label_appr
                    )
                    btnStatusWo.setOnClickListener {
                        dialogUpdateStatus()
                    }
                }
            }
            BaseParam.INPROGRESS -> {
                hideMore()
                binding.apply {
                    textStatus.text = BaseParam.COMPLETED
                    status.setTextColor(
                        ContextCompat.getColor(
                            this@DetailWoActivity,
                            R.color.colorYellow
                        )
                    )
                    bgStatus.background = ContextCompat.getDrawable(
                        this@DetailWoActivity,
                        R.drawable.bg_status_label_inprg
                    )
                    btnStatusWo.setOnClickListener {
                        dialogUpdateStatus()
                    }
                }
            }
            BaseParam.COMPLETED -> {
                binding.apply {
                    layoutStatus.visibility = GONE
                    status.setTextColor(
                        ContextCompat.getColor(
                            this@DetailWoActivity,
                            R.color.colorGreen
                        )
                    )
                    bgStatus.background = ContextCompat.getDrawable(
                        this@DetailWoActivity,
                        R.drawable.bg_status_label_comp
                    )
                    btnStatusWo.setOnClickListener {
                        dialogUpdateStatus()
                    }
                }
            }
            BaseParam.CLOSED -> {
                binding.apply {
                    layoutStatus.visibility = GONE
                    status.setTextColor(
                        ContextCompat.getColor(
                            this@DetailWoActivity,
                            R.color.colorGray2
                        )
                    )
                    bgStatus.background = ContextCompat.getDrawable(
                        this@DetailWoActivity,
                        R.drawable.bg_status_label_close
                    )
                    btnStatusWo.setOnClickListener {
                        dialogUpdateStatus()
                    }
                }
            }

            BaseParam.WAPPR -> {
                binding.apply {
                    layoutStatus.visibility = GONE
                    status.setTextColor(
                        ContextCompat.getColor(
                            this@DetailWoActivity,
                            R.color.brown
                        )
                    )
                    bgStatus.background = ContextCompat.getDrawable(
                        this@DetailWoActivity,
                        R.drawable.bg_status_label_wappr
                    )
                    btnStatusWo.setOnClickListener {
                        dialogUpdateStatus()
                    }
                }
            }

            else -> {
                binding.layoutStatus.visibility = GONE
            }
        }

//        binding.scanQr.setOnClickListener {
//            if (workorderStatus == BaseParam.COMPLETED) {
//                Toast.makeText(this, R.string.stat_complete, Toast.LENGTH_SHORT).show()
//            } else {
//                gotoListMaterial()
//            }
//        }

        binding.includeWorklog.worklog.setOnClickListener {
            goToWorkLog()
        }

        binding.includeMaterialPlan.materialPlan.setOnClickListener {
            goToMaterialPlan()
        }

        binding.includeMaterialActual.materialActual.setOnClickListener {
            goToMaterialActual()
        }

        binding.includeLongdesc.longdesc.setOnClickListener {
            gotoLongDescription()
        }

        binding.includeAssets.cardAsset.setOnClickListener {
            gotoListAsset()
        }
    }

    private fun hideMore() {
        binding.btnMore.visibility = GONE
        val params1 = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        params1.setMargins(0, 0, 0, 0)
        binding.textStatus.layoutParams = params1
    }

    private fun dialogUpdateStatus() {
        customDialogUtils.setTitle(R.string.information)
        customDialogUtils.setDescription(R.string.next_step)
        customDialogUtils.setRightButtonText(R.string.dialog_yes)
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
        customDialogUtils.setListener(this)
        customDialogUtils.show()
    }

    override fun onRightButton() {
        if (workorderStatus != null && workorderStatus == BaseParam.APPROVED && workorderNumber != null) {
            detailWoViewModel.updateWo(
                workorderId,
                workorderStatus,
                workorderNumber!!,
                workorderLongdesc,
                BaseParam.INPROGRESS
            )
        } else if (workorderStatus != null && workorderStatus == BaseParam.INPROGRESS && workorderNumber != null) {
            detailWoViewModel.updateWo(
                workorderId,
                workorderStatus,
                workorderNumber!!,
                workorderLongdesc,
                BaseParam.COMPLETED
            )
        }
//        workorderId.whatIfNotNull {
//            detailWoViewModel.uploadAttachments(it)
//        }
        gotoHome()
    }

    override fun onLeftButton() {
        customDialogUtils.dismiss()
    }

    override fun onMiddleButton() {
        customDialogUtils.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        customDialogUtils.dismiss()
    }

    override fun onPause() {
        super.onPause()
        customDialogUtils.dismiss()
    }

    override fun onBackPressed() {
        detailWoViewModel.removeScanner(workorderId!!)
        finish()
    }

    private fun gotoHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun startQRScanner(requestCode: Int) {
        IntentIntegrator(this@DetailWoActivity).apply {
            captureActivity = ScannerActivity::class.java
            setRequestCode(requestCode)
            initiateScan()
        }
    }
}