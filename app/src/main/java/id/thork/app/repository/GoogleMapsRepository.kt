package id.thork.app.repository

import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.network.api.GoogleMapsClient
import id.thork.app.network.response.google_maps.ResponseRoute
import timber.log.Timber

/**
 * Created by M.Reza Sulaiman on 18/02/21
 * Jakarta, Indonesia.
 */
class GoogleMapsRepository constructor(
    private val googleMapsClient: GoogleMapsClient
) {
    val TAG = GoogleMapsRepository::class.java.name

    suspend fun requestRoute(
        origin: String,
        destination: String,
        apikey: String,
        onSuccess: (ResponseRoute) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = googleMapsClient.requestRoute(origin, destination, apikey)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                onSuccess(response)
            }
        }
            .onError {
                Timber.tag(TAG).i("requestRoute() code: %s error: %s", statusCode.code, message())
                onError(message())
            }

            .onException {
                Timber.tag(TAG).i("loginByPerson() exception: %s", message())
                onError(message())
            }
    }
}