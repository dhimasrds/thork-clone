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

package id.thork.app.pages.profiles.setting.settings_pattern

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivitySettingsPatternBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.login_pattern.LoginPatternActivity
import id.thork.app.pages.server.ServerActivity
import id.thork.app.utils.StringUtils
import timber.log.Timber

class SettingsPatternActivity : BaseActivity(), CustomDialogUtils.DialogActionListener {
    private val TAG = SettingsPatternActivity::class.java.name

    private val viewModel: SettingsPatternViewModel by viewModels()
    private val binding: ActivitySettingsPatternBinding by binding(R.layout.activity_settings_pattern)
    private lateinit var customDialogUtils: CustomDialogUtils

    private var isDoPatternValidation = BaseParam.APP_FALSE
    private var patternState = BaseParam.APP_FALSE
    private var tempPattern = BaseParam.APP_EMPTY_STRING
    private val TAG_SETTING = "TAG_SETTING"


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@SettingsPatternActivity
            vm = viewModel
        }
        //Init Custom Dialog
        customDialogUtils = CustomDialogUtils(this)
        binding.patternLockView.addPatternLockListener(patternLockViewListener)
        binding.etProfileName.setText(R.string.settings_change_pattern_activity)
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnSwitchUser.setOnClickListener {
            viewModel.deleteUserSession()
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.validateUsername()
        viewModel.isPatttern.observe(this, Observer {
            isDoPatternValidation = it
            if (it == BaseParam.APP_FALSE) {
                binding.btnSwitchUser.visibility = View.GONE
            }
            Timber.d("isDoPattern %s", isDoPatternValidation)
        })

        viewModel.validatePattern.observe(this, Observer {
            if (it == BaseParam.APP_TRUE) {
                Timber.d("setupObserve() validatePattern(): %s", it)
                navigateLoginPattern()
            } else {
                showFailedDialog()
            }
        })

        viewModel.switchUser.observe(this, Observer {
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
                Timber.tag(TAG).d(
                    "patternLockViewListener() onComplete: %s",
                    PatternLockUtils.patternToString(binding.patternLockView, pattern)
                )
                if (isDoPatternValidation == BaseParam.APP_TRUE) {
                    Timber.tag(TAG).d(
                        "patternLockViewListener() isDoPatternValidation %s",
                        isDoPatternValidation
                    )
                    //validate pattern with user session
                    viewModel.validatePattern(
                        PatternLockUtils.patternToString(
                            binding.patternLockView,
                            pattern
                        )
                    )
                } else {
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
                    viewModel.setUserPattern(patternExisting)
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

    private fun navigateLoginPattern() {
        Timber.tag(TAG).d("navigateLoginPattern()")
        val intent = Intent(this, LoginPatternActivity::class.java)
        intent.putExtra("TAG_SETTING", TAG_SETTING)
        startActivity(intent)
        finish()
    }

    private fun navigateToServerAcitivity() {
        val intent = Intent(this, ServerActivity::class.java)
        startActivity(intent)
        finish()
    }
}