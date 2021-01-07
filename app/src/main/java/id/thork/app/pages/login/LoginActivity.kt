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

package id.thork.app.pages.login

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityLoginBinding
import id.thork.app.pages.login.element.LoginViewModel
import id.thork.app.pages.main.MainActivity
import id.thork.app.utils.CommonUtils
import timber.log.Timber

class LoginActivity : BaseActivity() {
    val TAG = LoginActivity::class.java.name

    val loginViewModel: LoginViewModel by viewModels()
    private val binding: ActivityLoginBinding by binding(R.layout.activity_login)

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@LoginActivity
            vm = loginViewModel
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.includeLoginContent.loginbt.setOnClickListener {
            loginViewModel.validateCredentials(
                binding.includeLoginContent.username.text.toString(),
                binding.includeLoginContent.password.text.toString()
            )
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        loginViewModel.success.observe(this, Observer { success ->
            Timber.tag(TAG).i("setupObserver() success: %s", success)
            CommonUtils.showToast(success, CommonUtils.POSITION_CENTER)
        })

        loginViewModel.error.observe(this, Observer { error ->
            Timber.tag(TAG).i("setupObserver() error: %s", error)
            CommonUtils.showToast(error, CommonUtils.POSITION_CENTER)
        })

        loginViewModel.firstLogin.observe(this, Observer { firstLogin ->
            Timber.tag(TAG).i("setupObserver() first login: %s", firstLogin)
            if (firstLogin) {
                // TODO
                // Ask to activate Pattern Login
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            finish()
        })

        loginViewModel.loginState.observe(this, Observer { loginState ->
            Timber.tag(TAG).i("setupObserver() state: %s", loginState)
        })
    }
}