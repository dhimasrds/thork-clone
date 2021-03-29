package id.thork.app.repository

import com.skydoves.sandwich.*
import com.skydoves.whatif.whatIfNotNull
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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

    suspend fun createTokenApiKey(headerParam: String, contentType: String, body: TokenApikey, onSuccess: (ResponseApiKey) -> Unit, onError: (String) -> Unit){
        val response = loginClient.createTokenApiKey(headerParam, contentType, body)
        response.suspendOnSuccess {
            data.whatIfNotNull{ response ->
                onSuccess(response)
            }
        }
            .onError {
                Timber.tag(TAG).i("createTokenApiKey() code: %s error: %s", statusCode.code, message())
                onError(message())
            }
            .onException {
                Timber.tag(TAG).i("createTokenApiKey() exception: %s", message())
                onError(message())
            }
    }

    suspend fun loginPerson(
        apikey: String,
        select: String,
        where: String,
        onSuccess: (UserResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = loginClient.loginByPerson(apikey, select, where)
        response.suspendOnSuccess {
            data.whatIfNotNull {response->
                //TODO
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

    suspend fun getTodo(id: Int) =
        loginClient.getTodo(id)


}