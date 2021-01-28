///*
// * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved.
// *
// * This software is the confidential and proprietary information of
// * This.ID. ("Confidential Information").
// *
// * Such Confidential Information shall not be disclosed and shall
// * use it only	 in accordance with the terms of the license agreement
// * entered into with This.ID; other than in accordance with the written
// * permission of This.ID.
// */
//
//package id.thork.app.di.module
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.net.ConnectivityManager
//import android.net.NetworkCapabilities
//import android.os.Build
//import dagger.Module
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import timber.log.Timber
//import java.net.HttpURLConnection
//import java.net.URL
//import javax.inject.Inject
//
//@Module
//@InstallIn(SingletonComponent::class)
//class NetworkConnectivity @Inject constructor(
//    private val context: Context,
//    private val appExecutors: AppExecutors,
//) : BroadcastReceiver() {
//    private val TAG = NetworkConnectivity::class.java.name
//
//    private val PING_SERVER = "http://clients3.google.com/generate_204";
//    private lateinit var callback: ConnectivityCallback
//
//    init {
//        Timber.tag(TAG).i("init() init NetworkConnectivity")
//    }
//
//    interface ConnectivityCallback {
//        fun onDetected(isConnected: Boolean)
//    }
//
//    override fun onReceive(context: Context, intent: Intent) {
//        if (callback != null) {
//            checkInternetConnection()
//        }
//    }
//
//    fun registerCallback(callback: ConnectivityCallback) {
//        this.callback = callback
//    }
//
//    @Synchronized
//    fun checkInternetConnection() {
//        Timber.tag(TAG).i("checkInternetConnection() callback: %s", callback)
//        appExecutors.networkIO().execute {
//            if (isInternetAvailable(context)) {
//                var connection: HttpURLConnection? = null
//                try {
//                    connection = URL(PING_SERVER)
//                        .openConnection() as HttpURLConnection
//                    connection.apply {
//                        setRequestProperty("User-Agent", "Android")
//                        setRequestProperty("Connection", "close")
//                        connectTimeout = 1000
//                        connect()
//                    }
//                    Timber.tag(TAG).i(
//                        "checkInternetConnection() response code: %s content: %s",
//                        connection.responseCode, connection.contentLength
//                    )
//
//                    val isConnected = (connection.responseCode == 204
//                            && connection.contentLength == 0)
//                    postCallback(callback, isConnected)
//                    connection.disconnect()
//                } catch (e: Exception) {
//                    Timber.tag(TAG).i("checkInternetConnection() error: %s", e)
//
//                    postCallback(callback, false)
//                    if (connection != null) connection.disconnect()
//                }
//            } else {
//                postCallback(callback, false)
//            }
//        }
//    }
//
//    @Synchronized
//    fun checkInternetConnection(paramCallBack: ConnectivityCallback) {
//        this.callback = paramCallBack
//        Timber.tag(TAG).i("checkInternetConnection() callback: %s", callback)
//        appExecutors.networkIO().execute {
//            if (isInternetAvailable(context)) {
//                var connection: HttpURLConnection? = null
//                try {
//                    connection = URL(PING_SERVER)
//                        .openConnection() as HttpURLConnection
//                    connection.apply {
//                        setRequestProperty("User-Agent", "Android")
//                        setRequestProperty("Connection", "close")
//                        connectTimeout = 1000
//                        connect()
//                    }
//                    Timber.tag(TAG).i(
//                        "checkInternetConnection() response code: %s content: %s",
//                        connection.responseCode, connection.contentLength
//                    )
//
//                    val isConnected = (connection.responseCode == 204
//                            && connection.contentLength == 0)
//                    postCallback(callback, isConnected)
//                    connection.disconnect()
//                } catch (e: Exception) {
//                    Timber.tag(TAG).i("checkInternetConnection() error: %s", e)
//                    postCallback(callback, false)
//                    if (connection != null) connection.disconnect()
//                }
//            } else {
//                postCallback(callback, false)
//            }
//        }
//    }
//
//    private fun postCallback(
//        callBack: ConnectivityCallback,
//        isConnected: Boolean
//    ) {
//        appExecutors.mainThread().execute { callBack.onDetected(isConnected) }
//    }
//
//    @Suppress("DEPRECATION")
//    private fun isInternetAvailable(context: Context): Boolean {
//        var result = false
//        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            cm?.run {
//                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
//                    result = when {
//                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//                        else -> false
//                    }
//                }
//            }
//        } else {
//            cm?.run {
//                cm.activeNetworkInfo?.run {
//                    if (type == ConnectivityManager.TYPE_WIFI) {
//                        result = true
//                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
//                        result = true
//                    }
//                }
//            }
//        }
//        return result
//    }
//}
