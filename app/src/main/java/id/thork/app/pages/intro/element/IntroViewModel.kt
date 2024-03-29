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

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.PreferenceManager
import kotlinx.coroutines.launch

class IntroViewModel @ViewModelInject constructor(
    val preferenceManager: PreferenceManager
) : LiveCoroutinesViewModel() {
    val TAG = IntroViewModel::class.java.name

    fun isFirstLaunch(): Boolean {
        return preferenceManager.getBoolean(BaseParam.APP_FIRST_LAUNCH)
    }

    fun disableFirstLaunch() {
        viewModelScope.launch {
            preferenceManager.putBoolean(BaseParam.APP_FIRST_LAUNCH, false)
        }
    }
}