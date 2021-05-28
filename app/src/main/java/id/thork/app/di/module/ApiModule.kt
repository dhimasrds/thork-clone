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

package id.thork.app.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.thork.app.di.module.workorder.WorkOrderModule
import id.thork.app.network.RetrofitBuilder
import id.thork.app.network.api.DoclinksApi
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    private val TAG = ApiModule::class.java.name

    @Provides
    @Singleton
    fun provideDoclinksApi(preferenceManager: PreferenceManager, httpLoggingInterceptor: HttpLoggingInterceptor): DoclinksApi {
        Timber.tag(WorkOrderModule.TAG).i("provideDoclinksApi() init global")
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(DoclinksApi::class.java)
    }
}