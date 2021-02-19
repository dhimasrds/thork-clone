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

import android.Manifest
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseApplication
import id.thork.app.databinding.ActivityMainBinding
import id.thork.app.extensions.setupWithNavController
import id.thork.app.messaging.Events
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.main.element.MainViewModel
import timber.log.Timber

@AndroidEntryPoint
class   MainActivity : BaseActivity(),  View.OnClickListener, CustomDialogUtils.DialogActionListener {
    val TAG = MainActivity::class.java.name

    val viewModel: MainViewModel by viewModels()

    private var currentNavController: LiveData<NavController>? = null
    private lateinit var customDialogUtils: CustomDialogUtils
    private lateinit var toolBar: Toolbar

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)

    override fun setupView() {
        super.setupView()
        setupMainView(binding.mainLayout)

        setupToolBar()
        setupBottomNavigationBar()

        binding.apply {
            lifecycleOwner = this@MainActivity
        }
        //Init custom dialog
        customDialogUtils = CustomDialogUtils(this)
        requestGrantPermissions()
    }

    private fun setupToolBar() {
        toolBar = binding.toolbar.wmsToolbar
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolBar.setNavigationIcon(R.drawable.ic_settings)
        binding.toolbar.toolbarTitle.text = getString(R.string.this_fsm)
        val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_filter)
        toolBar.overflowIcon = drawable
        toolBar.inflateMenu(R.menu.filter_menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.filter_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun setupBottomNavigationBar() {
        val bottomNavigationView = binding.bottomNavigationMain
        bottomNavigationView.menu.findItem(R.id.nav_graph_create).isEnabled = false
        val navGraphIds = listOf(R.navigation.nav_graph_wo, R.navigation.nav_graph_map)
        Timber.tag(TAG).i("setupBottomNavigationBar() navGraphIds: %s", navGraphIds.toString())

        val navController = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

        navController.observe(this,  { nav ->
            setupActionBarWithNavController(nav)
        })
        currentNavController = navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_add -> {
                Toast.makeText(this, "Clicked xxxx!", Toast.LENGTH_SHORT).show()
                viewModel.checkRepo()
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
    }

    /**
     * Request to Grant Permissions
     */
    private fun requestGrantPermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Timber.tag(TAG).i(
                            "requestGrantPermissions() report: %s",
                            report.grantedPermissionResponses.toString()
                        )
                    } else {
                        showDialog()
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        Timber.tag(TAG).d("requestGrantPermissions() Denied")
                        //TODO add event when permission permanet Denied
                        showDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
    }

    private fun showDialog() {
        customDialogUtils.setMiddleButtonText(R.string.dialog_yes)
            .setTittle(R.string.information)
            .setDescription(R.string.permission_location)
            .setListener(this)
        customDialogUtils.show()
    }

    override fun onRightButton() {
        Timber.tag(TAG).d("onRightButton()")
    }

    override fun onLeftButton() {
        Timber.tag(TAG).d("onLeftButton()")
    }

    override fun onMiddleButton() {
        customDialogUtils.dismiss()
    }
}