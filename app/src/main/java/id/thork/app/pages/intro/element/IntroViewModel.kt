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

package id.thork.app.pages.intro.element

import androidx.lifecycle.viewModelScope
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.PreferenceManager
import kotlinx.coroutines.launch

class IntroViewModel : LiveCoroutinesViewModel() {
    val TAG = IntroViewModel::class.java.name
    val preferenceManager: PreferenceManager = PreferenceManager(BaseApplication.context)

    fun getFirstLaunch() {
        viewModelScope.launch {
            preferenceManager.getBoolean(BaseParam.APP_FIRST_LAUNCH)
        }
    }

    fun launchMain(){
        viewModelScope.launch {
            preferenceManager.putBoolean(BaseParam.APP_FIRST_LAUNCH, false)
        }
    }
}