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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import id.thork.app.R
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.CustomInfoWindowForGoogleMap
import id.thork.app.utils.CommonUtils
import timber.log.Timber


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    val TAG = MapFragment::class.java.name
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var lastKnownLocation: Location? = null
    private val DEFAULT_ZOOM = 17
    private lateinit var customDialogUtils: CustomDialogUtils
    private lateinit var customInfoWindowForGoogleMap: CustomInfoWindowForGoogleMap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.context);
        customDialogUtils = CustomDialogUtils(view.context)
        customInfoWindowForGoogleMap = CustomInfoWindowForGoogleMap(view.context)

        getLocationPermission()
        // Inflate the layout for this fragment
        return view
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            map = googleMap
            map.setOnInfoWindowClickListener(this@MapFragment)
            map.setInfoWindowAdapter(customInfoWindowForGoogleMap)
            with(map) {
                uiSettings.isMyLocationButtonEnabled = false
                uiSettings.isMapToolbarEnabled = true
            }
        }
        renderMarker()
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
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
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true
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
                            map.isMyLocationEnabled = true
                            map.uiSettings.isCompassEnabled
                            map.uiSettings.isMyLocationButtonEnabled = true
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Timber.d("getDeviceLocation(): %s", e.message)
        }
    }

    override fun onInfoWindowClick(p0: Marker?){
        Timber.tag(TAG).d("onInfoWindowClick() %s", p0)
        val titleMarker = p0!!.title.toString()
        CommonUtils.showToast("onInfoWindowClick() title Marker: $titleMarker")

    }

    private fun renderMarker() {
        //TODO Hardcode marker
        val sydney = LatLng(-6.5920505,110.6813501)
        map.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("2021")
        )
    }
}