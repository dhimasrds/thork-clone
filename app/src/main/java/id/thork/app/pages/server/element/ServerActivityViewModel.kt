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

import android.content.Context
import android.util.Patterns
import android.webkit.URLUtil
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppExecutors
import id.thork.app.di.module.PreferenceManager
import id.thork.app.repository.LoginRepository
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL

class ServerActivityViewModel @ViewModelInject constructor(
    private val context: Context,
    private val loginRepository: LoginRepository,
    private val preferenceManager: PreferenceManager,
    private val appExecutors: AppExecutors,
) : LiveCoroutinesViewModel() {
    val TAG = ServerActivityViewModel::class.java.name

    private var serverReachable = false

    private val _state = MutableLiveData<Int>()
    val state: LiveData<Int> get() = _state

    private val _isReachable = MutableLiveData<Int>()
    val isReachable: LiveData<Int> get() = _isReachable

    private val _checkURL = MutableLiveData<Int>()
    val checkURL: LiveData<Int> get() = _checkURL

    private val _cacheUrl = MutableLiveData<String>()
    val cacheUrl: LiveData<String> get() = _cacheUrl

    private val workManager = WorkManager.getInstance(context)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    init {
        Timber.tag(TAG).i("init() loginRepository: %s", loginRepository)
        outputWorkInfos = workManager.getWorkInfosByTagLiveData("CREW_POSITION")
    }

    fun validateUrl(isHttps: Boolean, serverUrl: String) {
        val completeUrl = StringBuilder()
        if (isHttps) {
            completeUrl.append("https://")
        } else {
            completeUrl.append("http://")
        }
        val tmpServerUrl = serverUrl.replace("http://", "")
            .replace("https://", "")
        completeUrl.append(tmpServerUrl)
        val url = Patterns.WEB_URL.matcher(completeUrl).matches()
        Timber.d("raka validatorURL %s ", url)
        if (url) {
            if (URLUtil.isValidUrl(completeUrl.toString())) {
                Timber.tag(TAG).i("raka() serverUrl: %s", completeUrl.toString())
                doPingNetwork(completeUrl.toString())
                preferenceManager.putString(
                    BaseParam.APP_SERVER_ADDRESS,
                    beautifyServerUrl(completeUrl.toString())
                )
                _checkURL.postValue(BaseParam.APP_TRUE)
            }
        } else {
            _checkURL.postValue(BaseParam.APP_FALSE)
            _state.postValue(BaseParam.APP_FALSE)
        }

    }

    fun cacheServerUrl() {
        if (!preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS).isEmpty()) {
            _cacheUrl.value = preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS)
                .replace("http://", "")
                .replace("https://", "")
                .replace("/", "")
        }

    }

    fun beautifyServerUrl(serverUrl: String): String {
        val beautyUrl: String
        val lastString = serverUrl.substring(serverUrl.length - 1)
        if (lastString.equals("/")) {
            beautyUrl = serverUrl
        } else {
            beautyUrl = serverUrl + "/"
        }
        return beautyUrl
    }

    fun doPingNetwork(urlServer: String) {
        appExecutors.networkIO().execute {
            var connection: HttpURLConnection? = null
            try {
                connection = URL(urlServer)
                    .openConnection() as HttpURLConnection
                connection.apply {
                    setRequestProperty("User-Agent", "Android")
                    setRequestProperty("Connection", "close")
                    connectTimeout = 1000
                    connect()
                }
                Timber.tag(TAG).i(
                    "raka() response code: %s content: %s",
                    connection.responseCode, connection.contentLength
                )
                val isConnected = (connection.responseCode == 200
                        && connection.contentLength == 682)
                if (isConnected) {
                    _isReachable.postValue(BaseParam.APP_TRUE)
                    _state.postValue(BaseParam.APP_TRUE)
                }
                connection.disconnect()
            } catch (e: Exception) {
                _isReachable.postValue(BaseParam.APP_FALSE)
                _state.postValue(BaseParam.APP_FALSE)
                Timber.d("raka %s ", "error")
                if (connection != null) connection.disconnect()
            }
        }

    }

}