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

package id.thork.app.pages.main.element

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.databinding.FragmentMapBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.GoogleMapInfoWindow
import id.thork.app.utils.MapsUtils
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    CustomDialogUtils.DialogActionListener {

    val TAG = MapFragment::class.java.name
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var locationPermissionGranted = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private val bottomSheetToolTipFragment = BottomSheetToolTipFragment()
    private var lastKnownLocation: Location? = null
    private lateinit var customInfoWindowForGoogleMap: GoogleMapInfoWindow
    private lateinit var customDialogUtils: CustomDialogUtils
    private val mapViewModel: MapViewModel by activityViewModels()
    lateinit var sharedViewModel: MapViewModel
    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        val mapFragment: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext());
        customInfoWindowForGoogleMap = GoogleMapInfoWindow(requireContext())

        //init Dialog
        customDialogUtils = CustomDialogUtils(requireContext())

        getLocationPermission()
        getLocationChange()

        viewLifecycleOwner.lifecycleScope.launch {
            mapViewModel.isConnected()
        }
        mapViewModel.pruneWork()
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            map = googleMap
            onMapReadyState()
        }
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
        setupObserver()
    }

    // stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun onMapReadyState() {
        with(map) {
            setOnMarkerClickListener(this@MapFragment)
//            setInfoWindowAdapter(customInfoWindowForGoogleMap)
            uiSettings.isMyLocationButtonEnabled = false
            uiSettings.isMapToolbarEnabled = true
        }
    }

    fun setupObserver() {
        mapViewModel.listWo.observe(viewLifecycleOwner, {
            it.forEach {
                Timber.tag(TAG).d("setupObserver() ${it.wonum}")
                if (it.latitude != null && it.longitude != null) {
                    val woLatLng = LatLng(it.latitude!!.toDouble(), it.longitude!!.toDouble())
                    MapsUtils.renderWoMarker(map, woLatLng, it.wonum.toString())
                }
            }
        })
        mapViewModel.listMember.observe(viewLifecycleOwner, {
            it.forEach {
                if (!it.woserviceaddress.isNullOrEmpty()) {
                    val latitudeWo = it.woserviceaddress?.get(0)?.latitudey
                    val longitudeWo = it.woserviceaddress?.get(0)?.longitudex
                    if (latitudeWo != null && longitudeWo != null) {
                        val woLatLngOnline = LatLng(latitudeWo, longitudeWo)
                        Timber.d("setupObserver() Render Wo : ${it.wonum}")
                        MapsUtils.renderWoMarker(map, woLatLngOnline, it.wonum.toString())
                    }
                }
            }
        })

        mapViewModel.listMemberLocation.observe(viewLifecycleOwner, {
            it.forEach { member ->
                member.serviceaddress.whatIfNotNullOrEmpty {
                    val latitudeLocation = it.get(0).latitudey
                    val longitudeLocation = it.get(0).longitudex
                    if (latitudeLocation != null && longitudeLocation != null) {
                        val locationLatLng = LatLng(latitudeLocation, longitudeLocation)
                        MapsUtils.renderLocationMarker(
                            map,
                            locationLatLng,
                            member.location.toString()
                        )
                    }
                }
            }
        })

        mapViewModel.listMemberAsset.observe(viewLifecycleOwner, {
            it.forEach { member ->
                member.serviceaddress.whatIfNotNullOrEmpty {
                    val latitudeLocation = it.get(0).latitudey
                    val longitudeLocation = it.get(0).longitudex
                    if (latitudeLocation != null && longitudeLocation != null) {
                        val locationLatLng = LatLng(latitudeLocation, longitudeLocation)
                        MapsUtils.renderAssetMarker(
                            map,
                            locationLatLng,
                            member.assetnum.toString()
                        )
                    }
                }
            }
        })

        mapViewModel.outputWorkInfos.observe(this, workInfosObserver())
    }

    // Add this functions
    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            // Note that these next few lines grab a single WorkInfo if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkInfo is in one location.

            // If there are no matching work info, do nothing
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            listOfWorkInfo.forEach { workInfo ->
                val resultLaborcode = workInfo.outputData.getString("laborcode")
                val resultCrewId = workInfo.outputData.getString("crewId")
                val resultLongitude = workInfo.outputData.getString("longitude")
                val resultLatitude = workInfo.outputData.getString("latitude")
                val resultTag = workInfo.outputData.getString("tag")
                Timber.tag(TAG).i(
                    "workInfosObserver() myResult: %s, myResult2: %s , state: %s",
                    resultLaborcode.toString(),
                    resultCrewId.toString(),
                    workInfo.state
                )
                Timber.tag(TAG).i("workInfosObserver() resultLongitude: %s", resultLongitude)
                Timber.tag(TAG).i("workInfosObserver() resultLatitude: %s", resultLatitude)

                if (!resultLaborcode.isNullOrEmpty() && !resultCrewId.isNullOrEmpty()
                    && !resultLatitude.isNullOrEmpty() && !resultLongitude.isNullOrEmpty()
                    && !resultTag.isNullOrEmpty()
                ) {

                    val crewLatLng = LatLng(resultLatitude.toDouble(), resultLongitude.toDouble())
                    MapsUtils.renderCrewMarker(
                        map,
                        crewLatLng,
                        resultLaborcode,
                        resultCrewId,
                        resultTag
                    )
                    mapViewModel.pruneWork()
                }
            }

        }
    }


    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            == PackageManager.PERMISSION_GRANTED) {
            Timber.d("getLocationPermission() true")
            locationPermissionGranted = true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                    onMapReady(map)
                    updateLocationUI()
                }
            }
        }
    }

    private fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                with(map) {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                }
            } else {
                map.isMyLocationEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Timber.d("updateLocationUI() catch: %s", e.toString())
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener {
                    if (it.isSuccessful) {
                        lastKnownLocation = it.result
                        if (lastKnownLocation != null) {
                            with(map) {
                                isMyLocationEnabled = true
                            }
                            MapsUtils.renderCurrentLocation(map, lastKnownLocation)
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Timber.d("getDeviceLocation(): %s", e.message)
        }
    }

    private fun getLocationChange() {
        locationRequest = LocationRequest()
        locationRequest.interval = 5000 // 5 sec
        locationRequest.fastestInterval = 5000
        locationRequest.smallestDisplacement = 170f //170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                if (locationResult!!.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    Timber.d("onLocationChange() moved")
                    mapViewModel.postCrewPosition(
                        location.latitude.toString(),
                        location.longitude.toString()
                    )

                }
            }
        }
    }

    //start location updates
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun showDialog() {
        customDialogUtils.setMiddleButtonText(R.string.dialog_yes)
            .setTittle(R.string.information)
            .setDescription(R.string.permission_location)
            .setListener(this)
        customDialogUtils.show()
    }

    override fun onRightButton() {
        Timber.tag(TAG).d("onRightButton()")
    }

    override fun onLeftButton() {
        Timber.tag(TAG).d("onLeftButton()")
    }

    override fun onMiddleButton() {
        customDialogUtils.dismiss()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        if (locationPermissionGranted) {
            bottomSheetToolTipFragment.show(parentFragmentManager, "bottomsheetdialog")
            marker.whatIfNotNull {
                when (it.tag) {
                    BaseParam.APP_TAG_MARKER_WO -> {
                        Timber.d("raka %s", it.snippet)
                        Timber.d("raka %s", it.tag)
                        mapViewModel.setDataWo(it.snippet, it.tag.toString())
                    }

                    BaseParam.APP_TAG_MARKER_ASSET -> {
                        mapViewModel.setDataAsset(it.snippet, it.tag.toString())
                    }

                    BaseParam.APP_TAG_MARKER_LOCATION -> {
                        mapViewModel.setDataLocation(it.snippet, it.tag.toString())
                    }
                }
            }
        } else {
            showDialog()
        }
        return false
    }
}
