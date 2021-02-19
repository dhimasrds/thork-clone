/*
 * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * This.ID. ("Confidential Information").
 *
 * Such Confidential Information shall not be disclosed and shall
 * use it only	 in accordance with the terms of the license agreement
 * entered into with This.ID; other than in accordance with the written
 * permission of This.ID.
 */

package id.thork.app.pages.login.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.ResourceProvider
import id.thork.app.network.ApiParam
import id.thork.app.network.model.user.Member
import id.thork.app.network.model.user.UserResponse
import id.thork.app.pages.server.element.ServerActivityViewModel
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.repository.LoginRepository
import id.thork.app.utils.CommonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class LoginViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val resourceProvider: ResourceProvider,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {
    val TAG = ServerActivityViewModel::class.java.name

    private val _progressVisible = MutableLiveData<Boolean>(false)
    private val _success = MutableLiveData<String>()
    private val _error = MutableLiveData<String>()
    private val _loginState = MutableLiveData<Int>()
    private val _firstLogin = MutableLiveData<Boolean>()

    val progressVisible: LiveData<Boolean> get() = _progressVisible
    val success: LiveData<String> get() = _success
    val error: LiveData<String> get() = _error
    val loginState: LiveData<Int> get() = _loginState
    val firstLogin: LiveData<Boolean> get() = _firstLogin

    fun validateCredentials(username: String, password: String) {
        if (username.isNullOrEmpty()) {
            _error.postValue(resourceProvider.getString(R.string.username_error))
            return
        }

        if (password.isNullOrEmpty()) {
            _error.postValue(resourceProvider.getString(R.string.password_error))
            return
        }

        _progressVisible.value = true
        Timber.tag(TAG).i("validateCredentials() username: %s password: %s", username, password)
        validateWithActiveSession(username)

        val userHash = CommonUtils.encodeToBase64(username + ":" + password)
        Timber.tag(TAG).i("validateCredentials() user hash: %s", userHash)
        fetchUserData(userHash, username)
    }

    fun connectionNotAvailable() {
        _error.postValue(resourceProvider.getString(R.string.connection_not_available))
    }

    private fun validateWithActiveSession(username: String) {
        val userEntity: UserEntity? = loginRepository.findActiveSession()
        Timber.tag(TAG).i("validateWithActiveSession() userEntity: %s", userEntity)
        userEntity.whatIfNotNull {
            if (!userEntity?.username.equals(username)) {
                _error.postValue(resourceProvider.getString(R.string.username_invalid))
                return
            }
        }
    }

    private fun fetchUserData(userHash: String, username: String) {
        Timber.tag(TAG).i("fetchUserData()")
        val selectQuery = ApiParam.LOGIN_SELECT_ENDPOINT
        val whereQuery = ApiParam.LOGIN_WHERE_ENDPOINT + "\"" + username + "\"}"
        var userResponse = UserResponse()
        viewModelScope.launch(Dispatchers.IO) {
            //fetch user data via API
            loginRepository.loginPerson(userHash, selectQuery, whereQuery,
                onSuccess = {
                    userResponse = it
                },
                onError = {
                    Timber.tag(TAG).i("fetchUserData() error: %s", it)
                    _error.postValue(it)
                })

            //Save user into cache
            var member: Member
            userResponse.member.whatIfNotNullOrEmpty(
                whatIf = {
                    member = userResponse.member[0]
                    val isFirstLogin = userIsFirstLogin(member, username, userHash)
                    _firstLogin.postValue(isFirstLogin)
                    _loginState.postValue(BaseParam.APP_TRUE)
                    appSession.reinitUser()
                },
                whatIfNot = { _loginState.postValue(BaseParam.APP_FALSE) }
            )

            // When user founded and stored into cache, then hide progressbar
            _progressVisible.postValue(false)
        }
    }

    private fun userIsFirstLogin(member: Member, username: String, userHash: String): Boolean {
        val userEntity: UserEntity? = loginRepository.findUserByPersonUID(member.personuid)
        Timber.tag(TAG)
            .i("userIsFirstLogin() userEntity: %s personuid: %s", userEntity, member.personuid)
        userEntity.whatIfNotNull(
            whatIf = {
                userEntity!!.updatedDate = Date()
                userEntity.updatedBy = username
                userEntity.session = BaseParam.APP_TRUE
                userEntity.isPattern = BaseParam.APP_FALSE
                loginRepository.createUserSession(userEntity, username)
                return false
            },
            whatIfNot = {
                createNewUserSession(member, username, userHash)
                return true
            }
        )
        return false
    }

    private fun createNewUserSession(member: Member, username: String, userHash: String) {
        val userEntity = UserEntity()
        userEntity.createdDate = Date()
        userEntity.createdBy = username
        userEntity.updatedDate = Date()
        userEntity.updatedBy = username
        userEntity.session = BaseParam.APP_TRUE
        userEntity.isPattern = BaseParam.APP_FALSE
        userEntity.username = username
        userEntity.personUID = member.personuid

        member.maxuser.whatIfNotNullOrEmpty {
            userEntity.userid = member.maxuser[0].userid
        }

        member.organization.whatIfNotNullOrEmpty {
            userEntity.organization = member.organization[0].description
            userEntity.orgid = member.organization[0].orgid
        }

        member.site.whatIfNotNullOrEmpty {
            userEntity.site = member.site[0].description
            userEntity.siteid = member.site[0].siteid
        }

        member.labor.whatIfNotNullOrEmpty {
            userEntity.laborcode = member.labor[0].laborcode
        }

        userEntity.language = BaseParam.APP_DEFAULT_LANG_DETAIL
        userEntity.userHash = userHash
        //userEntity!!.server_address
        loginRepository.createUserSession(userEntity, username)
    }
}