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
package id.thork.app.pages.splash_screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.BuildConfig
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivitySplashScreenBinding
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.RootUtils
import id.thork.app.pages.server.ServerActivity

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity(),
    DialogUtils.DialogUtilsListener {
    private val splashScreenViewModel: SplashScreenViewModel by viewModels()
    private val binding: ActivitySplashScreenBinding by binding(R.layout.activity_splash_screen)

    override fun onCreate(savedInstanceState: Bundle?) {
        hideSystemUI()
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@SplashScreenActivity
        }
        setupVersion()

        setupLoadIntroPage()
    }

    private fun goToIntroActivity() {
        finish()
        startActivity(Intent(this, ServerActivity::class.java))
    }

    private fun goToServerActivity() {
        finish()
        startActivity(Intent(this, ServerActivity::class.java))
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }

    private fun setUpViews() {

    }

    private fun setupLoadIntroPage() {
        splashScreenViewModel.liveData.observe(this, Observer {
            when (it) {
                is SplashState.MainActivity -> {
                    goToServerActivity()
                }
                is SplashState.ServerActivity -> {
                    goToServerActivity()
                }
            }
        })

        if (isDeviceRooted()) {
            //we found indication of root
            showDialogExit()
        } else {
            val thread: Thread = object : Thread() {
                override fun run() {
                    try {
                        sleep(1200)
                        val intent = Intent(applicationContext, ServerActivity::class.java)
//                        intent = if () {
//                            Intent(applicationContext, IntroActivity::class.java)
//                        } else {
//                            Intent(applicationContext, IpConfigActivity::class.java)
//                        }
                        //get active user language
//                        if () {

//                        }
//                        else {
                        startActivity(intent)
                        finish()
//                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
            thread.start()
        }
    }

    private fun isDeviceRooted(): Boolean {
        val rootUtils = RootUtils(this)
        return rootUtils.isRootedWithoutTestKey
    }

    private fun setupVersion() {
        var versionName: String = BuildConfig.VERSION_NAME
        binding.txtVersion.text = versionName
    }

    private fun showDialogExit() {
        val dialogExit = DialogUtils(this)
        dialogExit
            .setTitles(R.string.dialog_root_title)
            .setMessage(R.string.dialog_root_subtitle)
            .setPositiveButtonLabel(R.string.dialog_yes)
            .setListener(this)
            .show()
    }

    private fun setupObserve() {
        splashScreenViewModel.liveData.observe(this, Observer {
            when (it) {
                is SplashState.MainActivity -> {
                    goToServerActivity()
                }
            }
        })
    }

    override fun onPositiveButton() {
        finish()
    }

    override fun onNegativeButton() {
        TODO("Not yet implemented")
    }

}