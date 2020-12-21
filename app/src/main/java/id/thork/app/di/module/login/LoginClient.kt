package id.thork.app.di.module.login

import id.thork.app.network.api.LoginApi
import javax.inject.Inject

class LoginClient @Inject constructor(
    private val loginApi: LoginApi) {

    suspend fun loginByPerson(select: String, where: String) =
        loginApi.loginByPerson(LEAN, select, where)

    suspend fun getTodo(id: Int) = loginApi.getTodo(id)

    companion object {
        private const val LEAN = 1
    }
}