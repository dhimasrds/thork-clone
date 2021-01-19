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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import id.thork.app.R


class MapFragment :Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment: SupportMapFragment? = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)


        // Inflate the layout for this fragment
        return view
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            mMap = googleMap
            val sydney = LatLng(-33.852, 151.211)
            mMap.addMarker(
                MarkerOptions()
                    .position(sydney)
                    .title("Marker in Sydney")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }

    }
}