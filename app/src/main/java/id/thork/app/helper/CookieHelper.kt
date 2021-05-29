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

package id.thork.app.helper

import android.content.Context
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.RetrofitBuilder
import id.thork.app.network.api.LoginApi
import id.thork.app.network.api.LoginClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.*
import kotlin.math.max

class CookieHelper constructor(val context: Context, val userHash: String) {
    val TAG = CookieHelper::class.java.name

    var maxauth: String = BaseParam.APP_EMPTY_STRING
    val preferenceManager: PreferenceManager
    val loginClient: LoginClient

    init {
        preferenceManager = PreferenceManager(context)
        userHash.let {
            maxauth = it
        }
        loginClient = LoginClient(provideLoginApi())
    }

    suspend fun generateCookieIfExpired(): String {
        var cookieString = BaseParam.APP_EMPTY_STRING
        try {
            runBlocking {
                launch {
                    val currentTime = Date().time
                    Timber.tag(TAG).d("generateCookieIfExpired() currentTime: %s", currentTime)
                    val lastUpdate = preferenceManager.getLong(BaseParam.APP_MX_COOKIE_LAST_UPDATE)
                    Timber.tag(TAG).d("generateCookieIfExpired() lastUpdate: %s", lastUpdate)
                    val sessionTimeoutLimit =
                        preferenceManager.getInt(BaseParam.APP_MX_COOKIE_TIMEOUT)
                    Timber.tag(TAG).d("generateCookieIfExpired() sessionTimeoutLimit: %s", sessionTimeoutLimit)

                    val isExpired = isExpired(lastUpdate, currentTime, sessionTimeoutLimit.toLong())
                    Timber.tag(TAG).d("generateCookieIfExpired() isExpired: %s", isExpired)
                    if (isExpired) {
                        val response = loginClient.login(maxauth)
                        response.suspendOnSuccess {
                            data.whatIfNotNull { response ->
                                val cookielist: List<String> = headers.values("Set-Cookie")
                                val jsessionid = cookielist[0].split(";").toTypedArray()[0]
                                jsessionid.whatIfNotNull {
                                    preferenceManager.putString(BaseParam.APP_MX_COOKIE, jsessionid)
                                    cookieString = jsessionid
                                    val sessiontimeout = response.sessiontimeout
                                    preferenceManager.putInt(
                                        BaseParam.APP_MX_COOKIE_TIMEOUT,
                                        sessiontimeout
                                    )
                                    preferenceManager.putLong(
                                        BaseParam.APP_MX_COOKIE_LAST_UPDATE,
                                        currentTime
                                    )
                                    Timber.tag(TAG)
                                        .d(
                                            "generateCookieIfExpired() new cookie: %s, timeout: %s last update: %s",
                                            cookieString, sessiontimeout, currentTime
                                        )
                                }
                            }
                        }.onError {
                            Timber.tag(TAG).d(
                                "generateCookieIfExpired() onError: %s code: %s",
                                message(), statusCode.code
                            )
                        }.onException {
                            Timber.tag(TAG)
                                .d("generateCookieIfExpired() onException: %s", message())
                        }
                    } else {
                        cookieString = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).d("generateCookieIfExpired() error: %s", e)
        }
        return cookieString
    }

    private fun isExpired(
        lastUpdate: Long,
        currentTime: Long,
        timeoutLimitInSecond: Long
    ): Boolean {
        val timeDiff = currentTime - lastUpdate
        val timeDiffInSecond = (timeDiff / 1000) + 60 //add additional 60 second threshold
        Timber.tag(TAG).d(
            "isExpired() timeDiffInSecond: %s timeoutLimitInSecond: %s",
            timeDiffInSecond, timeoutLimitInSecond
        )
        if (timeDiffInSecond > timeoutLimitInSecond) {
            return true
        }
        return false
    }

    private fun provideLoginApi(): LoginApi {
        val retrofit =
            RetrofitBuilder(preferenceManager, HttpLoggingInterceptor()).provideRetrofit()
        return retrofit.create(LoginApi::class.java)
    }
}