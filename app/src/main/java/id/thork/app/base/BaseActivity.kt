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

package id.thork.app.base

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.di.module.NetworkConnectivity
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity: AppCompatActivity(), NetworkConnectivity.ConnectivityCallback {
    protected inline fun <reified T : ViewDataBinding> binding(
        @LayoutRes resId: Int
    ): Lazy<T> = lazy { DataBindingUtil.setContentView<T>(this, resId) }

    @Inject
    lateinit var networkConnectivity: NetworkConnectivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupListener()
        setupObserver()

        networkConnectivity.registerCallback(this)
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        registerReceiver(networkConnectivity, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkConnectivity)
    }

    open fun setupView() {
    }

    open fun setupListener() {
    }

    open fun setupObserver() {
    }

    open fun onSuccess() {
    }

    open fun onError() {
    }

    open fun showToast() {
    }

    override fun onDetected(isConnected: Boolean) {
        Timber.tag(BaseApplication.TAG).i("onDetected() isConnected: %s", isConnected)
    }

//    companion object {
//        fun stuffDone(baseActivity: BaseActivity)  = baseActivity.showToast()
//
//        fun showToast(baseActivity: BaseActivity) {}
//
//        fun isConnected(isConnected: Boolean) {
//        stuffDone()
//        }
//    }
}