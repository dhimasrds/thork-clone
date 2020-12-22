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

import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.base.BaseParam
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.api.LoginApi
import id.thork.app.network.api.LoginClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

@Module
@InstallIn(ActivityRetainedComponent::class)
object LoginModule {
    val TAG = LoginModule::class.java.name

    @Provides
    @ActivityRetainedScoped
    fun provideLoginApi(retrofit: Retrofit, okHttpClient: OkHttpClient, preferenceManager: PreferenceManager):LoginApi {
        var serverUrl = preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS)
        if (serverUrl.isNullOrEmpty()) {
            serverUrl = BaseParam.BASE_SERVER_URL
        }
        Timber.tag(TAG).i("provideLoginApi() serverUrl: %s", serverUrl)
        retrofit.newBuilder().baseUrl(serverUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
            .client(okHttpClient)
            .build()
        return retrofit.create(LoginApi::class.java)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideLoginClient(loginApi: LoginApi): LoginClient {
        return LoginClient(loginApi)
    }
}