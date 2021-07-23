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

package id.thork.app.pages.login_pattern

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityLoginPatternBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.login_pattern.element.LoginPatternViewModel
import id.thork.app.pages.main.MainActivity
import id.thork.app.pages.profiles.setting.settings.SettingsActivity
import id.thork.app.pages.server.ServerActivity
import id.thork.app.utils.StringUtils
import timber.log.Timber
import java.util.*

class LoginPatternActivity : BaseActivity(), CustomDialogUtils.DialogActionListener {
    private val TAG = LoginPatternActivity::class.java.name

    private val loginPatternViewModel: LoginPatternViewModel by viewModels()
    private val binding: ActivityLoginPatternBinding by binding(R.layout.activity_login_pattern)
    private lateinit var customDialogUtils: CustomDialogUtils

    private val TAG_SETTING = "TAG_SETTING"
    private var isDoPatternValidation = BaseParam.APP_FALSE
    private var patternState = BaseParam.APP_FALSE
    private var tempPattern = BaseParam.APP_EMPTY_STRING
    private var changePatternSetting = BaseParam.APP_EMPTY_STRING

    @SuppressLint("NewApi")
    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@LoginPatternActivity
            vm = loginPatternViewModel
        }
        //Init Custom Dialog
        customDialogUtils = CustomDialogUtils(this)
        binding.patternLockView.addPatternLockListener(patternLockViewListener)
        val intent = intent
        val data = intent.getStringExtra(TAG_SETTING)
        if (data != null) {
            changePatternSetting = data
        }
    }

    override fun setupListener() {
        super.setupListener()
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        binding.tvVersion.text = BaseParam.APP_VERSION + pInfo.versionName
        binding.btnSwitchUser.setOnClickListener {
            loginPatternViewModel.switchUser()
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        loginPatternViewModel.validateUsername()
        loginPatternViewModel.username.observe(this, {
            if (changePatternSetting == TAG_SETTING) {
                binding.etProfileName.setText(R.string.change_pattern)
            } else {
                binding.etProfileName.text = it
            }
        })
        loginPatternViewModel.isSwitchUser.observe(this, {
            if (it == BaseParam.APP_TRUE_STRING) {
                binding.btnSwitchUser.visibility = View.VISIBLE
            }
        })

        loginPatternViewModel.isPatttern.observe(this, {
            isDoPatternValidation = it
            if (it == BaseParam.APP_FALSE) {
                binding.btnSwitchUser.visibility = View.INVISIBLE
            }

            Timber.d("isDoPattern %s", isDoPatternValidation)
        })

        loginPatternViewModel.validatePattern.observe(this, {
            if (it == BaseParam.APP_TRUE) {
                Timber.d("setupObserve() validatePattern(): %s", it)
                navigateToMainActivity()
            } else {
                showFailedDialog()
            }
        })

        loginPatternViewModel.switchUser.observe(this, {
            if (it == BaseParam.APP_TRUE) {
                navigateToServerAcitivity()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        customDialogUtils.dismiss()
    }

    override fun onResume() {
        super.onResume()
        customDialogUtils.dismiss()
    }

    private val patternLockViewListener: PatternLockViewListener =
        object : PatternLockViewListener {
            override fun onStarted() {
                Timber.tag(TAG).d("patternLockViewListener() onStarted drawing started")
            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {
                Timber.tag(TAG).d("patternLockViewListener() onProgress: %s", progressPattern)
            }

            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
//                Timber.tag(TAG).d(
//                    "patternLockViewListener() onComplete 1: %s",
//                    PatternLockUtils.patternToString(binding.patternLockView, pattern)
//                )
                if (changePatternSetting == TAG_SETTING) {
                    Timber.tag(TAG)
                        .d("patternLockViewListener() onComplete 1 %s", changePatternSetting)
                    reinputPattern(pattern)
                } else if (isDoPatternValidation == BaseParam.APP_TRUE) {
                    Timber.tag(TAG).d(
                        "patternLockViewListener() onComplete 2 %s",
                        isDoPatternValidation
                    )
                    //validate pattern with user session
                    loginPatternViewModel.validatePattern(
                        PatternLockUtils.patternToString(
                            binding.patternLockView,
                            pattern
                        )
                    )
                } else {
                    Timber.tag(TAG).d(
                        "patternLockViewListener() onComplete 3 %s",
                        isDoPatternValidation
                    )
                    reinputPattern(pattern)
                }
            }

            override fun onCleared() {
                Timber.tag(TAG).d("patternLockViewListener() onCleared")
            }
        }

    private fun clearPattern() {
        binding.patternLockView.clearPattern()
    }

    private fun resetPattern() {
        patternState = BaseParam.APP_FALSE
        tempPattern = BaseParam.APP_EMPTY_STRING
        binding.etProfileName.text =
            StringUtils.getStringResources(this, R.string.reset_pattern)
        clearPattern()
    }

    fun reinputPattern(pattern: MutableList<PatternLockView.Dot>?) {
        Timber.tag(TAG).d("patternLockViewListener() onComplete 1 %s", changePatternSetting)
        val patternExisting = PatternLockUtils.patternToString(binding.patternLockView, pattern)
        when (patternState) {
            BaseParam.APP_FALSE -> {
                tempPattern = patternExisting
                showReinputDialog()
            }

            BaseParam.APP_TRUE -> {
                if (!tempPattern.equals(patternExisting) && !patternExisting.isNullOrEmpty()) {
                    showFailedReinputDialog()
                } else {
                    loginPatternViewModel.setUserPattern(patternExisting)
                    Timber.tag(TAG).d("patternLockViewListener() onComplete has been save")
                }
            }
        }
    }

    private fun showReinputDialog() {
        customDialogUtils.setLeftButtonText(R.string.dialog_reset)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.pattern_title)
            .setDescription(R.string.reinput_pattern_message)
            .setListener(this)
        customDialogUtils.show()
    }

    private fun showFailedReinputDialog() {
        customDialogUtils.setLeftButtonText(R.string.dialog_reset)
            .setRightButtonText(R.string.dialog_repeat)
            .setTittle(R.string.pattern_title)
            .setDescription(R.string.reinput_pattern_failed)
            .setListener(this)
        customDialogUtils.show()
    }

    private fun showFailedDialog() {
        customDialogUtils.setMiddleButtonText(R.string.server_try_again)
            .setTittle(R.string.pattern_title)
            .setDescription(R.string.reinput_pattern_failed)
            .setListener(this)
        customDialogUtils.show()
    }

    override fun onRightButton() {
        if (patternState == BaseParam.APP_FALSE) {
            patternState = BaseParam.APP_TRUE
            binding.etProfileName.text =
                StringUtils.getStringResources(this, R.string.confim_pattern)
        }
        clearPattern()
        customDialogUtils.dismiss()
    }

    override fun onLeftButton() {
        customDialogUtils.dismiss()
        resetPattern()
    }

    override fun onMiddleButton() {
        customDialogUtils.dismiss()
        clearPattern()
    }

    private fun navigateToMainActivity() {
        Timber.tag(TAG).d("navigateToMainActivity()")
        if (changePatternSetting == TAG_SETTING) {
            Toast.makeText(this, R.string.settings_change_pattern_success, Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun navigateToServerAcitivity() {
        val intent = Intent(this, ServerActivity::class.java)
        startActivity(intent)
        finish()
    }
}