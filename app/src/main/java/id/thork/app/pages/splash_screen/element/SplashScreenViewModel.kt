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

package id.thork.app.pages.splash_screen.element

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
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class SplashScreenViewModel @ViewModelInject constructor(
    private val preferenceManager: PreferenceManager,
    private val appSession: AppSession,
    private val loginRepository: LoginRepository
) : LiveCoroutinesViewModel() {
    val TAG = SplashScreenViewModel::class.java.name

    val splashState: LiveData<SplashState> get() = _splashState
    val selectedLang: LiveData<String> get() = _selectedLang


    private val _splashState = MutableLiveData<SplashState>()
    private val _defaultLang = MutableLiveData<String>()
    private val _selectedLang = MutableLiveData<String>()


    init {
        validateLaunchState()
    }

    private fun validateLaunchState() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1200)
//            setupLanguage()

            setupLaunchState()
        }
    }

    fun validateLanguage(){
        _selectedLang.value = appSession.userEntity.language
    }

    private fun setupLanguage() {
        appSession.userEntity.whatIfNotNull(
            whatIf = {
                it.language.whatIfNotNull(
                    whatIf = {
                        _defaultLang.postValue(it)
                    },
                    whatIfNot = {
                        _defaultLang.postValue(BaseParam.APP_DEFAULT_LANG)
                    }
                )
            }
        )
    }

    private fun setupLaunchState() {
        val firstLaunch = preferenceManager.getBoolean(BaseParam.APP_FIRST_LAUNCH)
        firstLaunch.whatIf(
            whatIf = {
                Timber.tag(TAG).i("setupLaunchState() intro")
                _splashState.postValue(SplashState.IntroActivity())
            },
            whatIfNot = {
                setupUserSession()
            }
        )
    }

    private fun setupUserSession() {
        //if no active session, then go to Server Activity
        //if have active session, then go to Main Activity
        val userEntity: UserEntity? = loginRepository.findActiveSession()
        Timber.tag(TAG).i("setupUserSession() user entity: %s", userEntity)
        userEntity.whatIfNotNull(
            whatIf = {
                //for active session, check pattern is enable or not
                if (it.isPattern.equals(BaseParam.APP_TRUE)) {
                    _splashState.postValue(SplashState.LoginPatternActivity())
                } else {
                    _splashState.postValue(SplashState.LoginActivity())
                }
            },
            whatIfNot = {
                _splashState.postValue(SplashState.ServerActivity())
            }
        )
    }
}

sealed class SplashState {
    class IntroActivity : SplashState()
    class ServerActivity : SplashState()
    class LoginActivity : SplashState()
    class LoginPatternActivity : SplashState()
}