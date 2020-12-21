package id.thork.app.repository

import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.login.LoginClient
import timber.log.Timber
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val loginClient: LoginClient
) : BaseRepository {
    val TAG = LoginRepository::class.java.name

//    @WorkerThread
//    suspend fun loginByPerson(
//        select: String,
//        where: String,
//        onSuccess: () -> Unit,
//        onError: (String) -> Unit
//    ) = flow<UserResponse> {
//        Timber.tag(TAG).i("loginByPerson() select: %s where: %s", select, where)
//        val response = loginClient.loginByPerson(select, where)
//        Timber.tag(TAG).i("loginByPerson() response: %s", response)
//        response.suspendOnSuccess {
//            data.whatIfNotNull { response ->
//                var userResponses = response.member
//                Timber.tag(TAG).i("loginByPerson() user response: %s", userResponses.toString())
//                onSuccess()
//            }
//        }
//        .onError {
//            Timber.tag(TAG).i("loginByPerson() error: %s", message())
//            onError(message())
//        }
//        .onException {
//            Timber.tag(TAG).i("loginByPerson() exception: %s", message())
//            onError(message())
//        }
//    }.flowOn(Dispatchers.IO)

    suspend fun loginPerson(
        select: String,
        where: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        loginClient.loginByPerson(select, where)
            .onError {
                Timber.tag(TAG).i("loginByPerson() error: %s", message())
                onError(message())
            }
            .onException {
                Timber.tag(TAG).i("loginByPerson() exception: %s", message())
                onError(message())
            }
    }

    suspend fun getTodo(id: Int) =
        loginClient.getTodo(id)


}