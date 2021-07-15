package id.thork.app.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.base.MxResponse
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.api.LoginClient
import id.thork.app.network.model.user.LoginCookie
import id.thork.app.network.model.user.Logout
import id.thork.app.network.model.user.UserResponse
import id.thork.app.network.response.ErrorResponse.ErrorResponse
import id.thork.app.persistence.dao.*
import id.thork.app.persistence.entity.SysPropEntity
import id.thork.app.persistence.entity.SysResEntity
import id.thork.app.persistence.entity.UserEntity
import timber.log.Timber
import java.util.*


class LoginRepository constructor(
    private val loginClient: LoginClient,
    private val userDao: UserDao,
    private val preferenceManager: PreferenceManager,
    private val sysPropDao: SysPropDao,
    private val sysResDao: SysResDao,
    private val assetDao: AssetDao,
    private val woCacheDao: WoCacheDao,
    private val multiAssetDao: MultiAssetDao
) : BaseRepository {
    val TAG = LoginRepository::class.java.name

    fun findActiveSession(): UserEntity? {
        return userDao.findActiveSessionUser()
    }

    fun findUserByPersonUID(personUID: Int): UserEntity? {
        return userDao.findUserByPersonUID(personUID)
    }

    fun findUserByUsername(username: String): UserEntity? {
        return userDao.findUserByUsername(username)
    }

    fun createUserSession(userEntity: UserEntity, username: String): UserEntity {
        return userDao.createUserSession(userEntity, username)
    }

    fun saveLoginPattern(userEntity: UserEntity, username: String?) {
        username.whatIfNotNullOrEmpty {
            userDao.save(userEntity, it)
        }
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

    fun deleteWoPropertios() {
        return woCacheDao.remove()
    }

    fun createListSystemProperties(sysPropEntityList: List<SysPropEntity>): List<SysPropEntity> {
        return sysPropDao.saveListSystemProperties(sysPropEntityList)
    }

    fun deleteSystemResource() {
        return sysResDao.remove()
    }

    fun createListSystemResource(sysResEntityList: List<SysResEntity>) : List<SysResEntity> {
        return sysResDao.saveListSystemResource(sysResEntityList)
    }

    fun deleteAssetEntity() {
        return assetDao.remove()
    }

    fun deleteMultiAssetEntity() {
        return multiAssetDao.remove()
    }


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
                val cookielist: List<String> = headers.values("Set-Cookie")
                val jsessionid = cookielist[0].split(";").toTypedArray()[0]
                preferenceManager.putString(BaseParam.APP_MX_COOKIE, jsessionid)
                preferenceManager.putInt(
                    BaseParam.APP_MX_COOKIE_TIMEOUT,
                    response.sessiontimeout
                )
                preferenceManager.putLong(
                    BaseParam.APP_MX_COOKIE_LAST_UPDATE,
                    Date().time
                )
            }
        }.onError {
            Timber.tag(TAG).i("loginCookie() code: %s error: %s", statusCode.code, message())
            errorBody.whatIfNotNull(
                whatIf = {
                    val jsonString: String = it.string()
                    Timber.tag(TAG).i("loginCookie() jsonStringResponse: %s", jsonString)
                    val gson = Gson()
                    val jsonResponse: ErrorResponse =
                        gson.fromJson(jsonString, object : TypeToken<ErrorResponse?>() {}.type)
                    if (jsonResponse.oslcError?.spiReasonCode != null) {
                        val reasonCode: String = jsonResponse.oslcError?.spiReasonCode!!
                        Timber.tag(TAG)
                            .i(
                                "loginCookie() reasonCode: %s",
                                jsonResponse.oslcError?.spiReasonCode
                            )
                        var errorText = ""
                        errorText = when (reasonCode) {
                            MxResponse.BMXAA7901E -> "Incorrect Username or Password"
                            MxResponse.BMXAA0021E -> MxResponse.BMXAA0021E
                            else -> message()
                        }
                        onError(errorText)
                    }
                },
                whatIfNot = {
                    onError(message())
                }
            )
        }
            .onException {
                Timber.tag(TAG).i("loginCookie() exception: %s", message())
                onError(message())
            }

    }

    suspend fun logout(
        cookie: String,
        maxauth: String,
        onSuccess: (Logout) -> Unit,
        onError: (String) -> Unit,
    ) {
        val response = loginClient.logout(cookie, maxauth)
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

    suspend fun fetchSystemproperties(
        headerParam: String,
        select: String,
        onSuccess: (id.thork.app.network.response.system_properties.SystemProperties) -> Unit,
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