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

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.di.module.ConnectionLiveData
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity: AppCompatActivity() {
    protected inline fun <reified T : ViewDataBinding> binding(
        @LayoutRes resId: Int
    ): Lazy<T> = lazy { DataBindingUtil.setContentView<T>(this, resId) }

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupListener()
        setupObserver()
    }

    open fun setupView() {
    }

    open fun setupListener() {
    }

    open fun setupObserver() {
        Timber.tag(BaseApplication.TAG).i("setupObserver() isconnection live data instance: %s", connectionLiveData)
        connectionLiveData.observe(this, { isConnected ->
            isConnected?.let {
                Timber.tag(BaseApplication.TAG).i("setupObserver() isConnected: %s", isConnected)
                this.isConnected = isConnected
            }
        })
    }

    open fun onSuccess() {
    }

    open fun onError() {
    }

    open fun showToast() {
    }

    open fun onConnection(isConnected: Boolean) {

    }
}