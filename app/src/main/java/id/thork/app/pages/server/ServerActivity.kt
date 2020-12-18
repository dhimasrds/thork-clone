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

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityServerBinding
import timber.log.Timber

@AndroidEntryPoint
class ServerActivity : BaseActivity() {
    val TAG = ServerActivity::class.java.name

    val viewModel: ServerActivityViewModel by viewModels()
    private val binding: ActivityServerBinding by binding(R.layout.activity_server)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@ServerActivity
        }
        Timber.tag(TAG).i("onCreate() view model: %s", viewModel)
        viewModel.fetchApi()
    }
}