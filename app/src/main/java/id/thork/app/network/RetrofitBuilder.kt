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

package id.thork.app.network

import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import id.thork.app.base.BaseParam
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.NetworkModule
import id.thork.app.di.module.PreferenceManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

class RetrofitBuilder constructor(
    private val preferenceManager: PreferenceManager,
    private val httpLoggingInterceptor: HttpLoggingInterceptor
) {

    private val TAG = RetrofitBuilder::class.java.name

    fun provideRetrofit(): Retrofit {
        var serverAddress: String = preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS)
        if (serverAddress.isNullOrEmpty()) {
            serverAddress = "https://www.google.com"
        }
        Timber.tag(TAG).i("provideRetrofit() server address: %s", serverAddress)

        var retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
            .client(provideOkhttpClient(preferenceManager, httpLoggingInterceptor))
            .build()
        return retrofit
    }

    private fun provideOkhttpClient(
        preferenceManager: PreferenceManager,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        var serverAddress: String = preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS)
        if (serverAddress.isNullOrEmpty()) {
            serverAddress = "https://www.google.com"
        }
        Timber.tag(TAG)
            .i("provideOkHttpClient() init server address: %s", serverAddress)
        val httpRequestInterceptor = HttpRequestInterceptor()
        httpRequestInterceptor.setHost(serverAddress)
//        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
//            .addInterceptor(httpRequestInterceptor)
//            .addInterceptor(httpLoggingInterceptor)
//            .build()

//        return okHttpClient


        val okHttpUtils = OkHttpUtils()
        val okHttpClient = okHttpUtils.getTrustedOkHttpClient(httpRequestInterceptor, httpLoggingInterceptor)
        return okHttpClient
    }
}