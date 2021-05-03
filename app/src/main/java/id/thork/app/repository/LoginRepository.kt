package id.thork.app.repository

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skydoves.sandwich.*
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseRepository
import id.thork.app.base.MxResponse
import id.thork.app.network.api.LoginClient
import id.thork.app.network.model.user.LoginCookie
import id.thork.app.network.model.user.Logout
import id.thork.app.network.model.user.UserResponse
import id.thork.app.network.response.ErrorResponse.ErrorResponse
import id.thork.app.persistence.dao.UserDao
import id.thork.app.persistence.entity.UserEntity
import timber.log.Timber


class LoginRepository constructor(
    private val loginClient: LoginClient,
    private val userDao: UserDao,
) : BaseRepository {
    val TAG = LoginRepository::class.java.name

    fun findActiveSession(): UserEntity? {
        return userDao.findActiveSessionUser()
    }

    fun findUserByPersonUID(personUID: Int): UserEntity? {
        return userDao.findUserByPersonUID(personUID)
    }

    fun createUserSession(userEntity: UserEntity, username: String): UserEntity {
        return userDao.createUserSession(userEntity, username)
    }

    fun saveLoginPattern(userEntity: UserEntity, username: String?) {
        return userDao.save(userEntity, username!!)
    }

    fun deleteUserSession(userEntity: UserEntity) {
        return userDao.delete(userEntity)
    }


    @SuppressLint("NewApi")
    suspend fun loginPerson(
        select: String,
        where: String,
        onSuccess: (UserResponse) -> Unit,
        onError: (String) -> Unit,
        onException: (String) -> Unit,
    ) {
        val response = loginClient.loginByPerson(select, where)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                //Save user session into local cache
                onSuccess(response)
            }
        }.onError {
            Timber.tag(TAG).i("loginByPerson() code: %s error: %s", statusCode.code, message())
            onError(statusCode.code.toString())
            Timber.d("raka %s", errorBody)
        }
            .onException {
                Timber.tag(TAG).i("loginByPerson() exception: %s", message())
                onError(message())
            }
    }

    suspend fun loginCookie(
        maxauth: String,
        onSuccess: (LoginCookie) -> Unit,
        onError: (String) -> Unit,
    ) {
        val response = loginClient.login(maxauth)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                //Save user session into local cache
                onSuccess(response)
            }
        }.onError {
            Timber.tag(TAG).i("loginCookie() code: %s error: %s", statusCode.code, message())

            if (errorBody != null) {
                val jsonString: String = errorBody!!.string()
                Timber.tag(TAG).i("loginCookie() jsonStringResponse: %s", jsonString)
                val gson = Gson()
                val jsonResponse: ErrorResponse = gson.fromJson(jsonString, object : TypeToken<ErrorResponse?>() {}.type)
                if (jsonResponse.oslcError?.spiReasonCode != null) {
                    val reasonCode: String = jsonResponse.oslcError?.spiReasonCode!!
                    Timber.tag(TAG).i("loginCookie() reasonCode: %s", jsonResponse.oslcError?.spiReasonCode)
                    var errorText = ""
                    errorText = when(reasonCode){
                        MxResponse.BMXAA7901E -> "Incorrect Username or Password"
                        MxResponse.BMXAA0021E -> MxResponse.BMXAA0021E
                        else -> message()
                    }
                    onError(errorText)
                }
            } else {
                onError(message())
            }

            var errorText = ""
            errorText = when(statusCode){
                StatusCode.Unauthorized -> "Incorrect Username or Password"
                else -> message()
            }
            Timber.tag(TAG).i("createTokenApiKey() code: %s error: %s", statusCode.code, errorText)
            onError(errorText)
        }
            .onException {
                Timber.tag(TAG).i("loginCookie() exception: %s", message())
                onError(message())
            }

    }

    suspend fun logout(
        maxauth: String,
        onSuccess: (Logout) -> Unit,
        onError: (String) -> Unit,
    ) {
        val response = loginClient.logout(maxauth)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                //Save user session into local cache
                onSuccess(response)
            }
        }.onError {
            Timber.tag(TAG).i("logout() code: %s error: %s", statusCode.code, message())
            onError(message())
        }
            .onException {
                Timber.tag(TAG).i("logout() exception: %s", message())
                onError(message())
            }

    }
}