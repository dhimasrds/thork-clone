package id.thork.app.utils

import android.content.Context
import android.graphics.Color
import android.location.Location
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.helper.MapsLocation
import id.thork.app.network.response.google_maps.Distance
import id.thork.app.network.response.google_maps.Duration
import id.thork.app.network.response.google_maps.Leg
import id.thork.app.network.response.google_maps.ResponseRoute
import timber.log.Timber

/**
 * Created by M.Reza Sulaiman on 22/01/21
 * Jepara, Indonesia.
 */
object MapsUtils {
    private val crewListMarkers: MutableMap<String, Marker?> = hashMapOf()
    private var crewMarker: Marker? = null
    private var newCrewMarker: Marker? = null


    private const val LONG_DISTANCE_ZOOM = 11f
    private const val SHORT_DISTANCE_ZOOM = 17f
    private const val MAX_DISTANCE = 20000
    private val DEFAULT_ZOOM = 17


    fun renderWoMarker(googleMap: GoogleMap, latLng: LatLng, title: String) {
        val options = MarkerOptions()
            .position(latLng)
            .title(title)
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.node_wo))

        googleMap.addMarker(options)
            .tag = BaseParam.APP_TAG_MARKER_WO
    }


    fun renderCrewMarker(
        googleMap: GoogleMap,
        latLng: LatLng,
        title: String,
        crewId: String,
        tag: String
    ) {
        crewMarker = crewListMarkers.get(crewId)
        if (crewMarker != null) {
            crewMarker.whatIfNotNull(
                whatIf = {
                    when (tag) {
                        BaseParam.APP_TAG_MARKER_CREW_MOVE -> {
                            it.position = latLng
                        }

                        BaseParam.APP_TAG_MARKER_CREW_LOGOUT -> {
                            it.remove()
                            crewListMarkers.remove(crewId)
                        }
                    }
                }
            )
        } else {
            val options = MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_group_crew))
            newCrewMarker = googleMap.addMarker(options)
            crewListMarkers.put(crewId, newCrewMarker)
        }
    }

    fun renderCurrentLocation(googleMap: GoogleMap, lastKnownLocation: Location?) {
        with(googleMap) {
            lastKnownLocation.whatIfNotNull {
                uiSettings.isMyLocationButtonEnabled = true
                moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            it.latitude,
                            it.longitude
                        ), DEFAULT_ZOOM.toFloat()
                    )
                )
            }
        }
    }

    fun getLocationInfo(dataDirection: ResponseRoute?): MapsLocation? {
        if (dataDirection != null && !dataDirection.routes.isNullOrEmpty()) {
            val mapsLocation = MapsLocation()
            dataDirection.routes[0].legs.whatIfNotNullOrEmpty {
                val dataLegs: Leg = it[0]
                // get distance and duration
                val dataDistance: Distance? = dataLegs.distance
                val dataDuration: Duration? = dataLegs.duration
                mapsLocation.startAddress = dataLegs.startAddress
                mapsLocation.endAddress = dataLegs.endAddress
                dataDistance.whatIfNotNull {
                    mapsLocation.distanceText = it.text
                    it.value.whatIfNotNull { distanceValue ->
                        mapsLocation.distanceValue = distanceValue
                    }
                }
                dataDuration.whatIfNotNull {
                    mapsLocation.durationText = it.text
                    it.value.whatIfNotNull { durationValue ->
                        mapsLocation.durationValue = durationValue
                    }
                }
                return mapsLocation
            }
        }
        return null
    }


    fun renderMapDirection(
        mMap: GoogleMap?, context: Context, dataDirection: ResponseRoute?,
        startLatLng: LatLng?, endLatLng: LatLng?
    ) {
        if (mMap != null && dataDirection != null && !dataDirection.routes.isNullOrEmpty()) {
            dataDirection.routes[0].whatIfNotNull { dataDirectionRoutes ->
                dataDirectionRoutes.legs.whatIfNotNullOrEmpty {
                    // get Distance
                    val dataLegs: Leg = it[0]
                    dataLegs.distance.whatIfNotNull { distance ->
                        val dataDistance: Distance = distance
                        dataDirectionRoutes.overviewPolyline.whatIfNotNull { routePolyline ->
                            routePolyline.points.whatIfNotNullOrEmpty { polylinePoints ->
                                // get polyline
                                val polylinePoint: String = polylinePoints
                                // Decode
                                val decodePath = PolyUtil.decode(polylinePoint)
                                mMap.addPolyline(
                                    PolylineOptions().addAll(decodePath)
                                        .width(8f).color(Color.argb(255, 56, 167, 252))
                                ).isGeodesic = true

                                drawSourceMarker(mMap, startLatLng)
                                drawDestinationMarker(mMap, endLatLng)

                                val latLongBuilder = LatLngBounds.Builder()
                                latLongBuilder.include(startLatLng)
                                latLongBuilder.include(endLatLng)
                                var bounds = latLongBuilder.build()
                                val center = bounds.center
                                latLongBuilder.include(
                                    LatLng(
                                        center.latitude - 0.001f,
                                        center.longitude - 0.001f
                                    )
                                )
                                latLongBuilder.include(
                                    LatLng(
                                        center.latitude + 0.001f,
                                        center.longitude + 0.001f
                                    )
                                )

                                bounds = latLongBuilder.build()
                                val width = context.resources.displayMetrics.widthPixels
                                val height = context.resources.displayMetrics.heightPixels
                                val paddingMap = (width * 0.1).toInt()
                                val cu = CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    width,
                                    height,
                                    paddingMap
                                )
                                setupZoomPreferences(mMap, cu, dataDistance)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun drawSourceMarker(mMap: GoogleMap, startLatLng: LatLng?) {
        startLatLng.whatIfNotNull {
            // Add Marker
            mMap.addMarker(
                MarkerOptions()
                    .position(it)
                    .title("Origin")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_origin_wo))
            )
        }
    }

    private fun drawDestinationMarker(mMap: GoogleMap, endLatLng: LatLng?) {
        endLatLng.whatIfNotNull {
            mMap.addMarker(
                MarkerOptions()
                    .position(it)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.node_wo))
            )
        }
    }

    private fun setupZoomPreferences(mMap: GoogleMap, cu: CameraUpdate,  dataDistance: Distance) {
        Timber.d("setupZoomPreferences() Distance : %s", dataDistance.value)
        dataDistance.value.whatIfNotNull { distanceValue ->
            if (distanceValue > MAX_DISTANCE) {
                mMap.setMaxZoomPreference(LONG_DISTANCE_ZOOM)
            } else {
                mMap.setMaxZoomPreference(SHORT_DISTANCE_ZOOM)
            }
            mMap.animateCamera(cu)
        }
    }

}