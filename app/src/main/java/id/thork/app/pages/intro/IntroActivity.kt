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

package id.thork.app.pages.intro

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityMainIntroSliderBinding
import id.thork.app.pages.intro.element.IntroViewModel
import id.thork.app.pages.intro.element.IntroViewPagerAdapter
import id.thork.app.pages.server.ServerActivity

class IntroActivity : BaseActivity() {
    val TAG = IntroActivity::class.java.name
    private val SLIDER_STEP = 1
    private val binding: ActivityMainIntroSliderBinding by binding(R.layout.activity_main_intro_slider)
    private val viewModel: IntroViewModel by viewModels()

    private lateinit var bottomBars: Array<ImageView?>
    private lateinit var screens: IntArray
    private lateinit var myvpAdapter: IntroViewPagerAdapter

    var pageChangeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            coloredBars(position)

            if (position == screens.size - 1) {
                binding.next.visibility = View.GONE
                binding.skip.visibility = View.GONE
                binding.start.visibility = View.VISIBLE
            } else {
                binding.next.visibility = View.VISIBLE
                binding.skip.visibility = View.VISIBLE
                binding.start.visibility = View.GONE
            }
        }
    }

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@IntroActivity
            vm = viewModel
        }

        initView()
    }

    override fun setupListener() {
        super.setupListener()

        binding.next.setOnClickListener {
            nextAction()
        }

        binding.skip.setOnClickListener {
            navigateToServer()
        }

        binding.start.setOnClickListener {
            navigateToServer()
        }
    }

    private fun initView() {
        screens = intArrayOf(
            R.layout.layout_welcome1,
            R.layout.layout_welcome2,
            R.layout.layout_welcome3
        )

        myvpAdapter = IntroViewPagerAdapter(screens)
        binding.viewPager.adapter = myvpAdapter
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)

        if (viewModel.isFirstLaunch().equals(BaseParam.APP_FIRST_LAUNCH)) {
            navigateToServer()
            finish()
        }

        coloredBars(0)
    }

    private fun nextAction(){
        val i = binding.viewPager.currentItem + SLIDER_STEP
        if (i < screens.count()) {
            binding.viewPager.currentItem = i
        } else {
            navigateToServer()
        }
    }

    private fun navigateToServer() {
        viewModel.disableFirstLaunch()
        startActivity(Intent(this@IntroActivity, ServerActivity::class.java))
        finish()
    }

    private fun coloredBars(thisScreen: Int) {
        bottomBars = arrayOfNulls(screens.size)
        binding.layoutBars.removeAllViews()
        for (i in bottomBars.indices) {
            bottomBars[i] = ImageView(this)
            bottomBars.get(i)!!.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_dot, null))
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(10, 10, 10, 10)
            bottomBars.get(i)?.layoutParams = lp
            binding.layoutBars.addView(bottomBars.get(i))
        }
        if (bottomBars.isNotEmpty()) bottomBars.get(thisScreen)?.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_dot_blue, null))
    }
}