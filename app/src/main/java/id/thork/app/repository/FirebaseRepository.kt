package id.thork.app.repository

import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.network.api.FirebaseClient
import id.thork.app.network.response.firebase.ResponseFirebase
import okhttp3.RequestBody
import timber.log.Timber

/**
 * Created by M.Reza Sulaiman on 20/04/21
 * Jakarta, Indonesia.
 */
class FirebaseRepository constructor(
    private val firebaseClient: FirebaseClient
) {
    val TAG = FirebaseRepository::class.java.name

    suspend fun updateCrewPosition(
        headerParam: String,
        contentType: String,
        body: RequestBody,
        onSuccess: (ResponseFirebase) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = firebaseClient.updateCrewPosition(headerParam, contentType, body)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                onSuccess(response)
            }
        }
            .onError {
                Timber.tag(TAG)
                    .i("updateCrewPosition() code: %s error: %s", statusCode.code, message())
                onError(message())
            }

            .onException {
                Timber.tag(TAG).i("updateCrewPosition() exception: %s", message())
                onError(message())
            }
    }
}