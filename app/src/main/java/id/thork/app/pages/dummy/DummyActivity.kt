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

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.andrognito.patternlockview.utils.PatternLockUtils
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityDummyBinding
import timber.log.Timber

@AndroidEntryPoint
class DummyActivity : BaseActivity() {
    val TAG = DummyActivity::class.java.name

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

    fun showProgress(view: View) {
        Timber.tag(TAG).i("showProgress() view: %s", view.id)
        viewModel.validate()
    }
}