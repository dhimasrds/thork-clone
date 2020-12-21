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

package id.thork.app.pages.splash_screen

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.network.HttpRequestInterceptor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenViewModel @ViewModelInject constructor(
    private val httpRequestInterceptor: HttpRequestInterceptor,
    ) : LiveCoroutinesViewModel() {
    val liveData: LiveData<SplashState>
        get() = mutableLiveData
    private
    val mutableLiveData = MutableLiveData<SplashState>()

    init {
        val skipIntro: Boolean = true
        if (skipIntro) {

        } else {

        }
        GlobalScope.launch {
            delay(3000)
            httpRequestInterceptor.setHost("www.rejaluo.com")
            mutableLiveData.postValue(SplashState.MainActivity())
        }
    }
}

sealed class SplashState() {
    class MainActivity : SplashState()
    class ServerActivity: SplashState()
}