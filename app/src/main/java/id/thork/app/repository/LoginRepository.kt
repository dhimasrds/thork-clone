package id.thork.app.repository

import com.skydoves.sandwich.*
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseRepository
import id.thork.app.network.api.LoginClient
import id.thork.app.network.model.user.ResponseApiKey
import id.thork.app.network.model.user.TokenApikey
import id.thork.app.network.model.user.UserResponse
import id.thork.app.network.response.system_properties.SystemProperties
import id.thork.app.persistence.dao.SysPropDao
import id.thork.app.persistence.dao.UserDao
import id.thork.app.persistence.entity.SysPropEntity
import id.thork.app.persistence.entity.UserEntity
import timber.log.Timber

class LoginRepository constructor(
    private val loginClient: LoginClient,
    private val userDao: UserDao,
    private val sysPropDao: SysPropDao
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

    fun createSystemProperties(sysPropEntity: SysPropEntity, username: String) {
        return sysPropDao.save(sysPropEntity, username);
    }

    fun deleteSystemProperties() {
        return sysPropDao.remove()
    }

    fun createListSystemProperties(sysPropEntityList : List<SysPropEntity>): List<SysPropEntity> {
        return sysPropDao.saveListSystemProperties(sysPropEntityList)
    }

    suspend fun createTokenApiKey(
        headerParam: String,
        contentType: String,
        body: TokenApikey,
        onSuccess: (ResponseApiKey) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = loginClient.createTokenApiKey(headerParam, contentType, body)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                onSuccess(response)
            }
        }
            .onError {
//                Timber.tag(TAG)
//                    .i("createTokenApiKey() code: %s error: %s", statusCode.code, message())
//                onError(message())
                var errorText = ""
                errorText = when (statusCode) {
                    StatusCode.Unauthorized -> "Incorrect Username or Password"
                    else -> message()
                }
                Timber.tag(TAG)
                    .i("createTokenApiKey() code: %s error: %s", statusCode.code, errorText)
                onError(errorText)
            }
            .onException {
                val text = "Server is Down, Please a few minute!"
                Timber.tag(TAG).i("createTokenApiKey() exception: %s", text)
                onError(text)
            }
    }

    suspend fun loginPerson(
        apikey: String,
        select: String,
        where: String,
        onSuccess: (UserResponse) -> Unit,
        onError: (String) -> Unit,
        onException: (String) -> Unit
    ) {
        val response = loginClient.loginByPerson(apikey, select, where)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                //Save user session into local cache
                onSuccess(response)
            }
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

    suspend fun fetchSystemproperties(
        headerParam: String,
        select: String,
        onSuccess: (SystemProperties) -> Unit,
        onError: (String) -> Unit,
        onException: (String) -> Unit
    ) {
        val response = loginClient.getSystemProperties(headerParam, select)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                //Save system properties
                onSuccess(response)
            }
        }
            .onError {
                onError(message())
            }
            .onException {
                onException(message())
            }
    }
}