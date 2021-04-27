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
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityServerBinding
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.login.LoginActivity
import id.thork.app.pages.server.element.ServerActivityViewModel
import id.thork.app.utils.CommonUtils
import timber.log.Timber


@AndroidEntryPoint
class ServerActivity : BaseActivity(), DialogUtils.DialogUtilsListener {
    val TAG = ServerActivity::class.java.name

    val viewModel: ServerActivityViewModel by viewModels()
    private val binding: ActivityServerBinding by binding(R.layout.activity_server)

    private lateinit var dialogUtils: DialogUtils
    private lateinit var tryAgain: View
    private var exitApplication = false

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
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        binding.tvVersion.text = BaseParam.APP_VERSION + pInfo.versionName
        binding.includeServerContent.serverNext.setOnClickListener {
            viewModel.validateUrl(
                binding.includeServerContent.switchHttps.isChecked,
                binding.includeServerContent.serverUrl.text.toString()
            )
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.state.observe(this, {
            if (CommonUtils.isTrue(it)) {
                onSuccess()
            } else {
                onError()
            }
        })

        viewModel.cacheUrl.observe(this, { it ->
            binding.includeServerContent.serverUrl.setText(it)
        })

        viewModel.outputWorkInfos.observe(this, workInfosObserver())
    }

    // Add this functions
    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            // Note that these next few lines grab a single WorkInfo if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkInfo is in one location.

            // If there are no matching work info, do nothing
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            val workInfo = listOfWorkInfo[0]
            val myResult = workInfo.outputData.getString("data")
            val myResult2 = workInfo.outputData.getInt("angka", 23)

            Timber.tag(TAG).i(
                "workInfosObserver() myResult: %s myResult2: %s",
                myResult.toString(),
                myResult2
            )


//            if (workInfo.state.isFinished) {
//                showWorkFinished()
//            } else {
//                showWorkInProgress()
//            }
        }
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


    override fun onNotificationReceived(message: String) {
        super.onNotificationReceived(message)
        Timber.tag(TAG).i("onNotificationReceived() message: %s", message)
    }

    override fun onBackPressed() {
        Toast.makeText(this, R.string.exit_application, Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            exitApplication = false
        }, 2000)
        if (exitApplication) {
            finishAffinity()
            return
        }
        this.exitApplication = true
    }
}