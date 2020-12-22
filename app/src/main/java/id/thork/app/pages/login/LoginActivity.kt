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
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.MainActivity
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityLoginBinding
import id.thork.app.utils.CommonUtils
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    val TAG = LoginActivity::class.java.name

    val viewModel: LoginViewModel by viewModels()
    private val binding: ActivityLoginBinding by binding(R.layout.activity_login)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@LoginActivity
            vm = viewModel
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.includeLoginContent.loginbt.setOnClickListener {
            viewModel.validateCredentials(binding.includeLoginContent.username.text.toString(),
            binding.includeLoginContent.password.text.toString())
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel._success.observe(this, Observer {success ->
            Timber.tag(TAG).i("setupObserver() success: %s", success)
            if (success.equals("SUCCESS")) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            CommonUtils.showToast(success)
        })

        viewModel._error.observe(this, Observer { error ->
            Timber.tag(TAG).i("setupObserver() error: %s", error)
            CommonUtils.showToast(error)
        })
    }
}