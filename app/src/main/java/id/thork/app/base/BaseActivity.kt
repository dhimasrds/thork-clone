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
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import id.thork.app.R
import id.thork.app.di.module.ConnectionLiveData
import id.thork.app.di.module.ResourceProvider
import id.thork.app.helper.ConnectionState
import id.thork.app.workmanager.WoCoordinator
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {
    protected inline fun <reified T : ViewDataBinding> binding(
        @LayoutRes resId: Int
    ): Lazy<T> = lazy { DataBindingUtil.setContentView<T>(this, resId) }

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    @Inject
    lateinit var resourceProvider: ResourceProvider

    @Inject
    lateinit var workerCoordinator: WoCoordinator

    var isConnected = false

    var mainView: ViewGroup? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupListener()
        setupObserver()

        Timber.tag(BaseApplication.TAG).i("onCreate() coordinator instance: %s", workerCoordinator)
        workerCoordinator.ping()
    }

    open fun setupMainView(mainView: ViewGroup) {
        this.mainView = mainView
    }

    open fun setupView() {
    }

    open fun setupListener() {
    }

    open fun setupObserver() {
        Timber.tag(BaseApplication.TAG)
            .i("setupObserver() isconnection live data instance: %s", connectionLiveData)
        connectionLiveData.observe(this, { connectionState ->
            isConnected.let {
                Timber.tag(BaseApplication.TAG)
                    .i("setupObserver() connection state: %s", connectionState)
                defineConnectionState(connectionState)
            }
        })
    }

    open fun onSuccess() {
    }

    open fun onError() {
    }

    open fun showToast() {
    }

    open fun onGoodConnection() {
        Timber.tag(BaseApplication.TAG).i("onGoodConnection() connected")
    }

    open fun onSlowConnection() {
        //Show bottom toast connection information
        val toast = Toasty.warning(
            this,
            resourceProvider.getString(R.string.connection_slow),
            Toast.LENGTH_LONG,
            true
        )
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    open fun onLostConnection() {
//        Show bottom toast connection information
        val toast = Toasty.error(
            this,
            resourceProvider.getString(R.string.connection_not_available),
            Toast.LENGTH_LONG,
            true
        )
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    private fun defineConnectionState(connectionState: Int) {
        if (connectionState >= ConnectionState.SLOW.state) {
            onConnection(true)
            if (connectionState.equals(ConnectionState.SLOW.state)) {
                onSlowConnection()
            } else {
                onGoodConnection()
            }
            Timber.tag(BaseApplication.TAG).i(
                "defineConnectionState() connectionState: %s",
                connectionState
            )

        } else {
            onConnection(false)
            onLostConnection()
        }
    }

    private fun onConnection(isConnected: Boolean) {
        this.isConnected = isConnected
    }
}