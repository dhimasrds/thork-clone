package id.thork.app.repository

import androidx.annotation.WorkerThread
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.di.module.login.LoginClient
import id.thork.app.network.model.user.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val loginClient: LoginClient
) : Repository {

    @WorkerThread
    suspend fun loginByPerson(
        select: String,
        where: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = flow<UserResponse> {
        val response = loginClient.loginByPerson(select, where)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                var userResponses = response.member
                Timber.d(userResponses.toString())
                onSuccess()
            }
        }
        .onError {
            onError(message())
        }
        .onException {
            onError(message())
        }
    }.flowOn(Dispatchers.IO)

}