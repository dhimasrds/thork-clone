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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityMainBinding
import id.thork.app.extensions.setupWithNavController
import id.thork.app.pages.main.element.MainViewModel
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity(),  View.OnClickListener {
    val TAG = MainActivity::class.java.name

    val viewModel: MainViewModel by viewModels()

    private var currentNavController: LiveData<NavController>? = null
    private lateinit var toolBar: MaterialToolbar

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setupView() {
        super.setupView()
        toolBar = binding.toolbar
        setSupportActionBar(toolBar)

        setupBottomNavigationBar()

        binding.apply {
            lifecycleOwner = this@MainActivity
        }
    }

    private fun setupBottomNavigationBar() {
        val bottomNavigationView = binding.bottomNavigationMain
        bottomNavigationView.menu.findItem(R.id.nav_graph_create).isEnabled = false
        val navGraphIds = listOf(R.navigation.nav_graph_wo, R.navigation.nav_graph_map)
        Timber.tag(TAG).i("setupBottomNavigationBar() navGraphIds: %s", navGraphIds.toString())

        val navController = bottomNavigationView.setupWithNavController(navGraphIds = navGraphIds,
        fragmentManager =  supportFragmentManager,
        containerId =  R.id.nav_host_fragment,
        intent = intent)

        navController.observe(this, Observer { navController ->
            setupActionBarWithNavController(navController)
         })
        currentNavController = navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_add -> {
                Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}