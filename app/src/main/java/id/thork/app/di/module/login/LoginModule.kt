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

package id.thork.app.di.module.login

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.RetrofitBuilder
import id.thork.app.network.api.LoginApi
import id.thork.app.network.api.LoginClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(ActivityRetainedComponent::class)
object LoginModule {
    val TAG = LoginModule::class.java.name

    @Provides
    @ActivityRetainedScoped
    fun provideLoginApi(preferenceManager: PreferenceManager, httpLoggingInterceptor: HttpLoggingInterceptor): LoginApi {
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(LoginApi::class.java)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideLoginClient(loginApi: LoginApi): LoginClient {
        return LoginClient(loginApi)
    }
}