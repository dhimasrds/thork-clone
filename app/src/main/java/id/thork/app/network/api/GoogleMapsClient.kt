package id.thork.app.network.api

import javax.inject.Inject

/**
 * Created by M.Reza Sulaiman on 18/02/21
 * Jakarta, Indonesia.
 */
class GoogleMapsClient @Inject constructor(
    private val googleMapsApi: GoogleMapsApi
) {

    suspend fun requestRoute(origin: String, destination: String, apikey: String) =
        googleMapsApi.requestRoute(origin, destination, apikey)
}