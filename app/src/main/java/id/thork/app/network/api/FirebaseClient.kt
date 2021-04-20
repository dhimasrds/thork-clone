package id.thork.app.network.api

import okhttp3.RequestBody
import javax.inject.Inject

/**
 * Created by M.Reza Sulaiman on 20/04/21
 * Jakarta, Indonesia.
 */
class FirebaseClient @Inject constructor(
    private val firebaseApi: FirebaseApi
) {

    suspend fun updateCrewPosition(authorization: String, contentType: String, body: RequestBody) =
        firebaseApi.updateCrewPosition(authorization, contentType, body)
}