package id.thork.app.repository

import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseRepository
import id.thork.app.network.api.LoginClient
import id.thork.app.network.model.user.UserResponse
import id.thork.app.persistence.dao.UserDao
import id.thork.app.persistence.entity.UserEntity
import timber.log.Timber
import javax.inject.Singleton

@Singleton
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

    fun createUserSession(userEntity: UserEntity): UserEntity {
        return userDao.createUserSession(userEntity)
    }

    suspend fun loginPerson(
        headerParam: String,
        select: String,
        where: String,
        onSuccess: (UserResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = loginClient.loginByPerson(headerParam, select, where)
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