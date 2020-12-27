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
import androidx.lifecycle.viewModelScope
import com.skydoves.whatif.whatIf
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenViewModel @ViewModelInject constructor(
    private val preferenceManager: PreferenceManager,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {
    val TAG = SplashScreenViewModel::class.java.name

    val liveData: LiveData<SplashState>
        get() = mutableLiveData

    private
    val mutableLiveData = MutableLiveData<SplashState>()

    init {
        validateFirstLaunch()
    }

    fun validateFirstLaunch() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1200)
            var firstLaunch = false
            var language = BaseParam.APP_DEFAULT_LANG

            preferenceManager.getBoolean(BaseParam.APP_FIRST_LAUNCH).whatIf(
                whatIf = {
                    firstLaunch = true
                },
            )

            appSession.userEntity.whatIfNotNull(
                whatIf = {
                    it.language.whatIfNotNull(
                        whatIf = {
                            language = it
                        }
                    )
                },
            )

            firstLaunch.whatIf(
                whatIf = {
                    mutableLiveData.postValue(SplashState.ServerActivity())
                },
                whatIfNot = {
                    mutableLiveData.postValue(SplashState.IntroActivity())
                }
            )
        }
    }
}

sealed class SplashState() {
    class IntroActivity : SplashState()
    class ServerActivity : SplashState()
}