package id.thork.app.pages.detail_wo

import android.content.Intent
import android.location.Location
import androidx.activity.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityDetailWoBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.detail_wo.element.DetailWoViewModel
import id.thork.app.pages.list_material.ListMaterialActivity
import id.thork.app.pages.long_description.LongDescActivity
import id.thork.app.utils.MapsUtils
import id.thork.app.utils.StringUtils
import timber.log.Timber

class DetailWoActivity : BaseActivity(), OnMapReadyCallback {
    private val TAG = DetailWoActivity::class.java.name
    private val REQUEST_CODE_DETAIL = 0

    private val detailWoViewModel: DetailWoViewModel by viewModels()
    private val binding: ActivityDetailWoBinding by binding(R.layout.activity_detail_wo)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var customDialogUtils: CustomDialogUtils
    private lateinit var map: GoogleMap
    private var lastKnownLocation: Location? = null
    private var intentWonum: String? = null
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
        setupToolbarWithHomeNavigation(getString(R.string.wo_detail), navigation = false, filter = true)
        retrieveFromIntent()
    }

    override fun setupObserver() {
        super.setupObserver()
        detailWoViewModel.CurrentMember.observe(this, {
            val woPriority = StringUtils.NVL(it.wopriority, 0)
            binding.apply {
                wonum.text = it.wonum
                description.text = it.description
                status.text = it.status
                priority.text = StringUtils.createPriority(woPriority)

                workorderId = it.workorderid
                workorderNumber = it.wonum
                workorderStatus = it.status
                workorderLongdesc = it.description_longdescription
            }
            if (it.woserviceaddress?.get(0)?.latitudey != null && it.woserviceaddress.get(0).longitudex != null) {
                isRoute = BaseParam.APP_TRUE
                destinationLatLng = LatLng(
                    it.woserviceaddress.get(0).latitudey!!,
                    it.woserviceaddress.get(0).longitudex!!
                )
                destinationString =
                    "${it.woserviceaddress.get(0).latitudey!!},${it.woserviceaddress.get(0).longitudex!!}"
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

    }

    private fun retrieveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.APP_WONUM)
        detailWoViewModel.fetchWobyWonum(intentWonum!!)
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
        binding.scanQr.setOnClickListener {
            gotoListMaterial()
        }

        binding.longdesc.setOnClickListener {
            gotoLongDescription()
        }
    }

    private fun gotoListMaterial(){
        val intent = Intent(this, ListMaterialActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, workorderId)
        intent.putExtra(BaseParam.STATUS, workorderStatus)
        startActivity(intent)
    }

    private fun gotoLongDescription(){
        val intent = Intent(this, LongDescActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, workorderId)
        intent.putExtra(BaseParam.STATUS, workorderStatus)
        intent.putExtra(BaseParam.LONGDESC, workorderLongdesc)
        intent.putExtra(BaseParam.WONUM, workorderNumber)
        startActivityForResult(intent, REQUEST_CODE_DETAIL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_DETAIL && resultCode == RESULT_OK){
            valueLongDesc == data!!.getStringExtra("longdesc")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}