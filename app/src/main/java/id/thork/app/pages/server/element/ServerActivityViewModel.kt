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

package id.thork.app.pages.server.element

import android.webkit.URLUtil
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.PreferenceManager
import id.thork.app.repository.LoginRepository
import timber.log.Timber

class ServerActivityViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val preferenceManager: PreferenceManager,
) : LiveCoroutinesViewModel() {
    val TAG = ServerActivityViewModel::class.java.name

    private val _state = MutableLiveData<Int>()
    val state:LiveData<Int> get() = _state

    private val _cacheUrl = MutableLiveData<String>()
    val cacheUrl: LiveData<String> get() = _cacheUrl

    init {
        Timber.tag(TAG).i("init() loginRepository: %s", loginRepository)
    }

    fun validateUrl(serverUrl: String) {
        if (URLUtil.isValidUrl(serverUrl)) {
            Timber.tag(TAG).i("validateUrl() serverUrl: %s", serverUrl)
            preferenceManager.putString(BaseParam.APP_SERVER_ADDRESS, beautifyServerUrl(serverUrl))
            _state.postValue(BaseParam.APP_TRUE)
        }else {
            _state.postValue(BaseParam.APP_FALSE)
        }
    }

    fun cacheServerUrl() {
        if(!preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS).isEmpty()) {
            _cacheUrl.value = preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS)
        }

    }

    fun beautifyServerUrl(serverUrl: String): String {
        val beautyUrl: String
        val lastString = serverUrl.substring(serverUrl.length-1)
        if (lastString.equals("/")) {
            beautyUrl = serverUrl
        } else {
            beautyUrl = serverUrl + "/"
        }
        return beautyUrl
    }

}