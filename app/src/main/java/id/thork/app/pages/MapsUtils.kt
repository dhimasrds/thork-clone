package id.thork.app.pages

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Created by M.Reza Sulaiman on 22/01/21
 * Jepara, Indonesia.
 */
object MapsUtils {

    fun renderMarker(googleMap: GoogleMap, latLng: LatLng, title: String) {
        googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(title)
        )
    }

}