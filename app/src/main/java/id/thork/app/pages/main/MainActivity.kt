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

package id.thork.app.pages.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityMainBinding
import id.thork.app.pages.main.element.MainViewModel
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    val TAG = MainActivity::class.java.name

    val viewModel: MainViewModel by viewModels()

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun setupView() {
        super.setupView()
        binding.bottomNavigationMain.menu.findItem(R.id.nav_mynews).isEnabled = false
        binding.apply {
            lifecycleOwner = this@MainActivity
        }
    }

    override fun setupListener() {
        super.setupListener()
    }

    override fun setupObserver() {
        super.setupObserver()
    }

}