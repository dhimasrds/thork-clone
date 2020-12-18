package id.thork.app.repository

import androidx.annotation.WorkerThread
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.login.LoginClient
import id.thork.app.network.model.user.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val loginClient: LoginClient
) : BaseRepository {
    val TAG = LoginRepository::class.java.name

    @WorkerThread
    suspend fun loginByPerson(
        select: String,
        where: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = flow<UserResponse> {
        Timber.tag(TAG).i("loginByPerson() select: %s where: %s", select, where)
        val response = loginClient.loginByPerson(select, where)
        Timber.tag(TAG).i("loginByPerson() response: %s", response)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                var userResponses = response.member
                Timber.tag(TAG).i("loginByPerson() user response: %s", userResponses.toString())
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