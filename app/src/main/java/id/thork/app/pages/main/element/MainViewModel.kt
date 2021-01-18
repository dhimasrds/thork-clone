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

package id.thork.app.pages.main.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.network.api.LoginApi
import id.thork.app.repository.LoginRepository
import retrofit2.Retrofit
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val retrofit: Retrofit,
    private val loginApi: LoginApi
    ) : LiveCoroutinesViewModel() {
    val TAG = MainViewModel::class.java.name


    fun checkRepo() {
        Timber.tag(TAG).i("checkRepo() loginRepository: %s retrofit: %s", loginRepository, retrofit)
        Timber.tag(TAG).i("checkRepo() loginapi: %s", loginApi)

    }
}