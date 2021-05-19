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

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.thork.app.base.BaseParam
import id.thork.app.di.module.PreferenceManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStream
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
@GlideModule
class GlideOkHttpUtils @Inject constructor(
    private val context: Context): AppGlideModule() {

    lateinit var httpRequestInterceptor: HttpRequestInterceptor

    private fun setupInterceptor() {
        val preferenceManager = PreferenceManager(context)
        var serverAddress: String = preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS)
        if (serverAddress.isNullOrEmpty()) {
            serverAddress = "https://www.google.com"
        }
        httpRequestInterceptor = HttpRequestInterceptor()
        httpRequestInterceptor.setHost(serverAddress)
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        setupInterceptor()
        val okHttpUtils = OkHttpUtils()
        val okGlideClient: OkHttpClient = okHttpUtils.getTrustedOkHttpClient(
            httpRequestInterceptor = httpRequestInterceptor,
            httpLoggingInterceptor = HttpLoggingInterceptor()
        )
        val factory: OkHttpUrlLoader.Factory = OkHttpUrlLoader.Factory(okGlideClient)
        registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }
}