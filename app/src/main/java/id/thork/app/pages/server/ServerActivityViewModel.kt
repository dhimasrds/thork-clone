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

import android.webkit.URLUtil
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.network.HttpRequestInterceptor
import id.thork.app.network.model.Todo
import id.thork.app.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import timber.log.Timber

class ServerActivityViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val httpRequestInterceptor: HttpRequestInterceptor,
    private val okHttpClient: OkHttpClient
) : LiveCoroutinesViewModel() {
    val TAG = ServerActivityViewModel::class.java.name

    val _todo = MutableLiveData<Todo>()
    val _success = MutableLiveData<Boolean>()
    val _error = MutableLiveData<Boolean>()

    val _state = MutableLiveData<Int>()
    init {
        Timber.tag(TAG).i("init() loginRepository: %s", loginRepository)
    }

    fun validateUrl(serverUrl: String) {
        if (URLUtil.isValidUrl(serverUrl)) {
            httpRequestInterceptor.setHost(beautifyServerUrl(serverUrl))
            _state.postValue(BaseParam.APP_TRUE)
        }else {
            _state.postValue(BaseParam.APP_FALSE)
        }
    }

    fun beautifyServerUrl(serverUrl: String): String {
        var beautyUrl: String
        var lastString = serverUrl.substring(serverUrl.length-1)
        if (lastString.equals("/")) {
            beautyUrl = serverUrl
        } else {
            beautyUrl = serverUrl + "/"
        }
        return beautyUrl
    }

    fun addApiBaseUrl(baseUrl: String) {
        httpRequestInterceptor.setHost("www.talian.id")
    }

    fun fetchApi() {
        Timber.tag(TAG).i("fetchApi() loginRepository: %s", loginRepository)
//        launchOnViewModelScope {
//            this.loginRepository.loginByPerson("spi:locationsit","spi:maxuser{spi:loginid=\"this\"}",
//                onSuccess = { Timber.tag(TAG).i("init() success")},
//                onError = {Timber.tag(TAG).i("init() error")}).asLiveData()
//        }

        viewModelScope.launch(Dispatchers.IO) {
            val jsonBody = "{ username: \"THIS\", token: \"1234567890\"}"
            Timber.tag(TAG).i("fetchApi() json: %s", jsonBody)
            val output = loginRepository.loginPerson("spi:locationsit",
                "spi:maxuser{spi:loginid=\"this\"}",
                onSuccess = { Timber.tag(TAG).i("init() success") },
                onError = { Timber.tag(TAG).i("init() error") })
            Timber.tag(TAG).i("fetchApi2() output: %s", output)
        }
        Timber.tag(TAG).i("fetchApi3() loginRepository: %s", loginRepository)
    }


    fun fetchSecondApi() {
        Timber.tag(TAG).i("fetchSecondApi() loginRepository: %s", loginRepository)
        viewModelScope.launch(Dispatchers.IO) {
            val jsonBody = "{ username: \"THIS\", token: \"1234567890\"}"
            Timber.tag(TAG).i("fetchApi() json: %s", jsonBody)
            val output = loginRepository.loginPerson("spi:locationsit",
                "spi:maxuser{spi:loginid=\"this\"}",
                onSuccess = { Timber.tag(TAG).i("init() success") },
                onError = { Timber.tag(TAG).i("init() error") })
            Timber.tag(TAG).i("fetchApi2() output: %s", output)
        }
        Timber.tag(TAG).i("fetchApi3() loginRepository: %s", loginRepository)
    }

//    fun finalFetchApi() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//
//            } catch (throwable: Throwable) {
//                when (throwable) {
//                    is IOException -> {
//                        _error.postValue("Network Error")
//                    }
//                    is HttpException -> {
//                        val code = throwable.code()
//                        val errorResponse = throwable.message()
//                        _error.postValue("Error $code $errorResponse")
//                    }
//                    else -> {
//                        val errorResponse = throwable.message
//                        _error.postValue("Error $errorResponse")
//                    }
//                }
//            }
//        }
//    }
//
//    fun getTodo(id: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val result = loginRepository.getTodo(id)
//                _todo.postValue(result)
//            } catch (throwable: Throwable) {
//                when (throwable) {
//                    is IOException -> {
//                        _error.postValue("Network Error")
//                    }
//                    is HttpException -> {
//                        val code = throwable.code()
//                        val errorResponse = throwable.message()
//                        _error.postValue("Error $code $errorResponse")
//                    }
//                    else -> {
//                        val errorResponse = throwable.message
//                        _error.postValue("Error $errorResponse")
//                    }
//                }
//            }
//        }
//
//    }
}