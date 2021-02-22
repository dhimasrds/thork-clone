package id.thork.app.helper

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

/**
 * Created by M.Reza Sulaiman on 19/02/21
 * Jakarta, Indonesia.
 */
class MapsLocation {
    var startLatLng: LatLng? = null
    var destinationLatLng: LatLng? = null
    var startAddress: String? = null
    var endAddress: String? = null
    var distanceText: String? = null
    var distanceValue = 0
    var durationText: String? = null
    var durationValue = 0
    var googleMap: GoogleMap? = null
}