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

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import id.thork.app.helper.ConnectionState
import id.thork.app.utils.NetworkUtils
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@Module
@InstallIn(ActivityRetainedComponent::class)
class ConnectionLiveData @Inject constructor(
    val context: Context,
    private val appExecutors: AppExecutors,
) : LiveData<Int>() {
    private val TAG = ConnectionLiveData::class.java.name

    private val PING_SERVER = "http://clients3.google.com/generate_204";

    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var connectivityManagerCallback: ConnectivityManager.NetworkCallback

    private val networkRequestBuilder: NetworkRequest.Builder = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)

    /**
     * used to check internet speed
     */
    private var startPingTime: Long = 0
    private var endPingTime: Long = 0

    override fun onActive() {
        super.onActive()
        Timber.tag(TAG).i("onActive() %s", true)
        updateConnection()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> connectivityManager.registerDefaultNetworkCallback(
                getConnectivityMarshmallowManagerCallback()
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> marshmallowNetworkAvailableRequest()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> lollipopNetworkAvailableRequest()
            else -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    context.registerReceiver(
                        networkReceiver,
                        IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
                    ) // android.net.ConnectivityManager.CONNECTIVITY_ACTION
                }
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
        } else {
            context.unregisterReceiver(networkReceiver)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun lollipopNetworkAvailableRequest() {
        connectivityManager.registerNetworkCallback(
            networkRequestBuilder.build(),
            getConnectivityLollipopManagerCallback()
        )
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun marshmallowNetworkAvailableRequest() {
        connectivityManager.registerNetworkCallback(
            networkRequestBuilder.build(),
            getConnectivityMarshmallowManagerCallback()
        )
    }

    private fun getConnectivityLollipopManagerCallback(): ConnectivityManager.NetworkCallback {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Timber.tag(TAG).i("getConnectivityLollipopManagerCallback() lollipop available");
                    doPingNetwork()
                }

                override fun onLost(network: Network) {
                    Timber.tag(TAG).i("getConnectivityLollipopManagerCallback() lollipop NOT available");
                    postValue(ConnectionState.DISCONNECT.state)
                }
            }
            return connectivityManagerCallback
        } else {
            throw IllegalAccessError("Accessing wrong API version")
        }
    }

    private fun getConnectivityMarshmallowManagerCallback(): ConnectivityManager.NetworkCallback {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    networkCapabilities.let { capabilities ->
                        if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(
                                NetworkCapabilities.NET_CAPABILITY_VALIDATED
                            )
                        ) {
                            Timber.tag(TAG).i("getConnectivityLollipopManagerCallback() marsmallow available");
                            doPingNetwork()
                        }
                    }
                }

                override fun onLost(network: Network) {
                    Timber.tag(TAG).i("getConnectivityLollipopManagerCallback() marsmallow NOT available");
                    postValue(ConnectionState.DISCONNECT.state)
                }
            }
            return connectivityManagerCallback
        } else {
            throw IllegalAccessError("Accessing wrong API version")
        }
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateConnection()
        }
    }

    @Suppress("DEPRECATION")
    private fun updateConnection() {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork?.isConnected == true) {
            doPingNetwork()
        } else {
            postValue(ConnectionState.DISCONNECT.state)
        }
    }

    private fun doPingNetwork() {
        startPingTime = System.currentTimeMillis()
        Timber.tag(TAG).i("doPingNetwork() startPingTime: %s", startPingTime)
        appExecutors.networkIO().execute {
            var connection: HttpURLConnection? = null
            try {
                connection = URL(PING_SERVER)
                    .openConnection() as HttpURLConnection
                connection.apply {
                    setRequestProperty("User-Agent", "Android")
                    setRequestProperty("Connection", "close")
                    connectTimeout = 1000
                    connect()
                }
                Timber.tag(TAG).i(
                    "doPingNetwork() response code: %s content: %s",
                    connection.responseCode, connection.contentLength
                )
                val isConnected = (connection.responseCode == 204
                        && connection.contentLength == 0)
                if (isConnected) {
                    endPingTime = System.currentTimeMillis()
                    postValue(NetworkUtils.getConnectionState(startPingTime, endPingTime))
                }
                connection.disconnect()
            } catch (e: Exception) {
                Timber.tag(TAG).i("doPingNetwork() error: %s", e)
                postValue(ConnectionState.DISCONNECT.state)
                if (connection != null) connection.disconnect()
            }
        }

    }
}