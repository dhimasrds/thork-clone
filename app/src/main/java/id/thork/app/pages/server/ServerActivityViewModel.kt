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

package id.thork.app.pages.server

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.asLiveData
import com.squareup.inject.assisted.AssistedInject
import id.thork.app.MainViewModel
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ServerActivityViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository
): LiveCoroutinesViewModel() {
    val TAG = ServerActivityViewModel::class.java.name

    init {
        Timber.tag(TAG).i("init() loginRepository: %s", loginRepository)
    }

    fun fetchApi() {
        Timber.tag(TAG).i("fetchApi() loginRepository: %s", loginRepository)
        CoroutineScope(Dispatchers.Main).launch {
            loginRepository.loginByPerson("spi:locationsit","spi:maxuser{spi:loginid=\"this\"}",
                onSuccess = { Timber.tag(TAG).i("init() success")},
                onError = {Timber.tag(TAG).i("init() error")}).asLiveData()
        }
        Timber.tag(TAG).i("fetchApi2() loginRepository: %s", loginRepository)

//        launchOnViewModelScope {
//            this.loginRepository.loginByPerson("spi:locationsit","spi:maxuser{spi:loginid=\"this\"}",
//                onSuccess = { Timber.tag(TAG).i("init() success")},
//                onError = {Timber.tag(TAG).i("init() error")}).asLiveData()
//        }
    }

}