package id.thork.app.utils

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import id.thork.app.R
import id.thork.app.base.BaseParam

/**
 * Created by M.Reza Sulaiman on 22/01/21
 * Jepara, Indonesia.
 */
object MapsUtils {

    fun renderWoMarker(googleMap: GoogleMap, latLng: LatLng, title: String) {
        val options = MarkerOptions()
            .position(latLng)
            .title(title)
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.node_wo))

        googleMap.addMarker(options)
            .tag = BaseParam.APP_TAG_MARKER_WO
    }


    fun renderCrewMarker(googleMap: GoogleMap, latLng: LatLng, title: String) {
        val options = MarkerOptions()
            .position(latLng)
            .title(title)
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_group_crew))

        googleMap.addMarker(options)
            .tag = BaseParam.APP_TAG_MARKER_CREW
    }

}