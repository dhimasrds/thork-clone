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
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.databinding.FragmentMapBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.GoogleMapInfoWindow
import id.thork.app.pages.detail_wo.DetailWoActivity
import id.thork.app.utils.MapsUtils
import timber.log.Timber

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
    CustomDialogUtils.DialogActionListener {

    val TAG = MapFragment::class.java.name
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var lastKnownLocation: Location? = null
    private val DEFAULT_ZOOM = 17
    private lateinit var customInfoWindowForGoogleMap: GoogleMapInfoWindow
    private lateinit var customDialogUtils: CustomDialogUtils
    private val mapViewModel: MapViewModel by activityViewModels()
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
        binding.apply {
            lifecycleOwner = this@MapFragment
            vm = mapViewModel
        }
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            map = googleMap
            with(map) {
                setOnInfoWindowClickListener(this@MapFragment)
                setInfoWindowAdapter(customInfoWindowForGoogleMap)
                uiSettings.isMyLocationButtonEnabled = false
                uiSettings.isMapToolbarEnabled = true
            }
        }
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
        setupObserver()
    }

    fun setupObserver() {
        mapViewModel.fetchListWo()
        mapViewModel.listWo.observe(viewLifecycleOwner, {
            it.forEach {
                if (it.latitude != null && it.longitude != null) {
                    val woLatLng = LatLng(it.latitude!!.toDouble(), it.longitude!!.toDouble())
                    MapsUtils.renderWoMarker(map, woLatLng, it.wonum.toString())
                }
            }
        })
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

    override fun onInfoWindowClick(marker: Marker?) {
        if (locationPermissionGranted) {
            navigateToDetailWo(marker)
        } else {
            showDialog()
        }
    }

    private fun navigateToDetailWo(marker: Marker?) {
        val intent = Intent(activity, DetailWoActivity::class.java)
        intent.putExtra(BaseParam.APP_WONUM, marker!!.title.toString())
        startActivity(intent)
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
}