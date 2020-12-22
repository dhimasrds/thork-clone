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

package id.thork.app.pages.dummy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityDummyBinding
import id.thork.app.databinding.ActivityLoginBinding
import id.thork.app.pages.login.LoginViewModel

@AndroidEntryPoint
class DummyActivity : BaseActivity() {
    val viewModel: DummyViewModel by viewModels()
    private  lateinit var binding: ActivityDummyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dummy)
        binding.apply {
            binding.lifecycleOwner = this@DummyActivity
            vm = viewModel
        }
    }

    fun showProgress(view:View) {
        viewModel.validate()
    }
}