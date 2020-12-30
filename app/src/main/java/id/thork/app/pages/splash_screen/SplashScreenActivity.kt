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
import id.thork.app.BuildConfig
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivitySplashScreenBinding
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.RootUtils
import id.thork.app.pages.intro.IntroActivity
import id.thork.app.pages.login.LoginActivity
import id.thork.app.pages.login_pattern.LoginPatternActivity
import id.thork.app.pages.server.ServerActivity
import id.thork.app.pages.splash_screen.element.SplashScreenViewModel
import id.thork.app.pages.splash_screen.element.SplashState
import timber.log.Timber

class SplashScreenActivity : BaseActivity(),
    DialogUtils.DialogUtilsListener {
    private val TAG = SplashScreenActivity::class.java.name

    private val splashScreenViewModel: SplashScreenViewModel by viewModels()
    private val binding: ActivitySplashScreenBinding by binding(R.layout.activity_splash_screen)

    override fun onCreate(savedInstanceState: Bundle?) {
        hideSystemUI()
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@SplashScreenActivity
            vm = splashScreenViewModel
        }
        setupVersion()
    }

    private fun goToIntroActivity() {
        //TODO
        //Change into IntroActivity
        finish()
        startActivity(Intent(this, IntroActivity::class.java))
    }

    private fun goToServerActivity() {
        finish()
        startActivity(Intent(this, ServerActivity::class.java))
    }

    private fun goToLoginActivity() {
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun goToLoginPatternActivity() {
        finish()
        startActivity(Intent(this, LoginPatternActivity::class.java))
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

    private fun setupLoadIntroPage() {
        if (isDeviceRooted()) {
            //we found indication of root
            showDialogExit()
            return
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

    override fun setupObserver() {
        super.setupObserver()
        setupLoadIntroPage()
        splashScreenViewModel.splashState.observe(this, Observer {
            when (it) {
                is SplashState.IntroActivity -> {
                    Timber.tag(TAG).i("setupObserver() intro")
                    goToIntroActivity()
                }
                is SplashState.ServerActivity -> {
                    Timber.tag(TAG).i("setupObserver() server")
                    goToServerActivity()
                }
                is SplashState.LoginActivity -> {
                    Timber.tag(TAG).i("setupObserver() login")
                    goToLoginActivity()
                }
                is SplashState.LoginPatternActivity -> {
                    Timber.tag(TAG).i("setupObserver() login pattern")
                    goToLoginPatternActivity()
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