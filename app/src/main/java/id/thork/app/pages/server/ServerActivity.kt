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
package id.thork.app.pages.server

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityServerBinding
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.login.LoginActivity
import id.thork.app.pages.server.element.ServerActivityViewModel
import id.thork.app.utils.CommonUtils
import timber.log.Timber


class ServerActivity : BaseActivity(), DialogUtils.DialogUtilsListener {
    val TAG = ServerActivity::class.java.name

    val viewModel: ServerActivityViewModel by viewModels()
    private val binding: ActivityServerBinding by binding(R.layout.activity_server)

    private lateinit var dialogUtils: DialogUtils
    private lateinit var tryAgain: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@ServerActivity
        }
        Timber.tag(TAG).i("onCreate() view model: %s", viewModel)
        viewModel.cacheServerUrl()
    }

    override fun setupListener() {
        super.setupListener()
        binding.includeServerContent.serverNext.setOnClickListener {
            viewModel.validateUrl(binding.includeServerContent.serverUrl.text.toString())
        }
    }

    override fun setupObserver() {
        viewModel.state.observe(this,  {
            if (CommonUtils.isTrue(it)) {
                onSuccess()
            } else {
                onError()
            }
        })

        viewModel.cacheUrl.observe(this, { it ->
            binding.includeServerContent.serverUrl.setText(it)
        })
    }

    override fun onSuccess() {
        super.onSuccess()
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onError() {
        super.onError()
        showDialog()
    }

    override fun onPositiveButton() {
    }

    override fun onNegativeButton() {
    }

    fun showDialog() {
        dialogUtils = DialogUtils(this)
        dialogUtils.setInflater(R.layout.activity_server_dialog, null, layoutInflater)
            .create()
            .setRounded(true)
        tryAgain = dialogUtils.setViewId(R.id.try_again)
        tryAgain.setOnClickListener {
            dialogUtils.dismiss()
        }

        dialogUtils.show()
    }
}