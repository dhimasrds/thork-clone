package id.thork.app.repository

import android.annotation.SuppressLint
import com.skydoves.sandwich.*
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseRepository
import id.thork.app.network.api.LoginClient
import id.thork.app.network.model.user.ResponseApiKey
import id.thork.app.network.model.user.TokenApikey
import id.thork.app.network.model.user.UserResponse
import id.thork.app.persistence.dao.UserDao
import id.thork.app.persistence.entity.UserEntity
import timber.log.Timber

class LoginRepository constructor(
        private val loginClient: LoginClient,
        private val userDao: UserDao
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
            onException: (String) -> Unit
    ) {
        val response = loginClient.loginByPerson( select, where)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                //Save user session into local cache
                onSuccess(response)
            }
        }.onSuccess {

        }
                .onError {
                    Timber.tag(TAG).i("loginByPerson() code: %s error: %s", statusCode.code, message())
                    onError(message())
                }
                .onException {
                    Timber.tag(TAG).i("loginByPerson() exception: %s", message())
                    onError(message())
                }
    }

    suspend fun loginCookie(
            maxauth: String,
            onSuccess: (id.thork.app.network.model.user.LoginCookie) -> Unit,
            onError: (String) -> Unit,
    ) {
        val response = loginClient.login(maxauth)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                //Save user session into local cache
                onSuccess(response)
            }
        }.onSuccess {
            val cookielist: List<String> = headers.values("Set-Cookie")
            val jsessionid = cookielist[0].split(";").toTypedArray()[0]
//            Timber.tag(TAG).i("loginByPerson() success decode: %s", decodedString)
        }
                .onError {
                    Timber.tag(TAG).i("loginCookie() code: %s error: %s", statusCode.code, message())
                    onError(message())
                }
                .onException {
                    Timber.tag(TAG).i("loginCookie() exception: %s", message())
                    onError(message())
                }

    }
}