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

package id.thork.app.pages.login

import android.content.Intent
import android.os.Handler
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.ResourceProvider
import id.thork.app.network.ApiParam
import id.thork.app.network.model.user.UserResponse
import id.thork.app.pages.server.ServerActivityViewModel
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.repository.LoginRepository
import id.thork.app.utils.CommonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val resourceProvider: ResourceProvider
) : LiveCoroutinesViewModel() {
    val TAG = ServerActivityViewModel::class.java.name

    val _progressVisible = MutableLiveData<Boolean>(false)
    val progressVisible: LiveData<Boolean> get() = _progressVisible

    val _success = MutableLiveData<String>()
    val _error = MutableLiveData<String>()

    init {
        Timber.tag(TAG).i("init()")
    }

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

        val selectQuery = ApiParam.LOGIN_SELECT_ENDPOINT
        val whereQuery = ApiParam.LOGIN_WHERE_ENDPOINT + "\"" + username + "\"}"
        fetchUserData(userHash, selectQuery, whereQuery)
    }

    private fun validateWithActiveSession(username: String) {
        Timber.tag(TAG).i("validateWithActiveSession()")
        val userEntities: List<UserEntity> = loginRepository.findActiveSession()
        if (!userEntities.isNullOrEmpty()) {
            val userEntity = userEntities[0]
            if (!userEntity.username.equals(username)) {
                _error.postValue(resourceProvider.getString(R.string.username_invalid))
                return
            }
        }
    }

    private fun fetchUserData(headerParam: String, selectQuery: String, whereQuery: String) {
        Timber.tag(TAG).i("fetchUserData()")
        var userResponse: UserResponse = UserResponse()
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.loginPerson(headerParam, selectQuery, whereQuery,
                onSuccess = {
                    Timber.tag(TAG).i("fetchUserData() success: %s", it.member.toString())
                    userResponse = it
                },
                onError = { Timber.tag(TAG).i("fetchUserData() error: %s", it) })
            _progressVisible.postValue(false)

            if (userResponse != null) {
                _success.postValue("SUCCESS")
            }
        }
        _success.postValue("ON LOADING")
    }
}