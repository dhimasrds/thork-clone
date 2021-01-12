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
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseApplication.Constants.context
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityMainIntroSliderBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.intro.element.IntroViewModel
import id.thork.app.pages.intro.element.MyViewPagerAdapter
import id.thork.app.pages.server.ServerActivity

class IntroActivity : BaseActivity() {
    val TAG = IntroActivity::class.java.name
    private val binding: ActivityMainIntroSliderBinding by binding(R.layout.activity_main_intro_slider)
    private val viewModel: IntroViewModel by viewModels()
    val preferenceManager: PreferenceManager = PreferenceManager(context)

    private lateinit var bottomBars: Array<ImageView?>
    private lateinit var screens: IntArray
    private lateinit var myvpAdapter: MyViewPagerAdapter

    var pageChangeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            ColoredBars(position)

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        handlerOnClick()
    }

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@IntroActivity
            vm = viewModel
        }
    }

    private fun initView() {
        screens = intArrayOf(
            R.layout.layout_welcome1,
            R.layout.layout_welcome2,
            R.layout.layout_welcome3
        )

        myvpAdapter = MyViewPagerAdapter(screens)
        binding.viewPager.adapter = myvpAdapter
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)

        if (!preferenceManager.getBoolean(BaseParam.APP_FIRST_LAUNCH)) {
            launchMain()
            finish()
        }

        ColoredBars(0)
    }

    private fun handlerOnClick() {
        binding.next.setOnClickListener {
            val i = getItem(+1)
            if (i < screens.count()) {
                binding.viewPager.currentItem = i
            } else {
                launchMain()
            }
        }

        binding.skip.setOnClickListener {
            launchMain()
        }

        binding.start.setOnClickListener {
            launchMain()
        }
    }

    private fun launchMain() {
        preferenceManager.putBoolean(BaseParam.APP_FIRST_LAUNCH, false)
        startActivity(Intent(this@IntroActivity, ServerActivity::class.java))
        finish()
    }

    private fun ColoredBars(thisScreen: Int) {
        bottomBars = arrayOfNulls(screens.size)
        binding.layoutBars.removeAllViews()
        for (i in bottomBars.indices) {
            bottomBars[i] = ImageView(this)
            bottomBars.get(i)!!.setImageDrawable(resources.getDrawable(R.drawable.ic_dot))
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(10, 10, 10, 10)
            bottomBars.get(i)?.setLayoutParams(lp)
            binding.layoutBars.addView(bottomBars.get(i))
        }
        if (bottomBars.size > 0) bottomBars.get(thisScreen)?.setImageDrawable(resources.getDrawable(R.drawable.ic_dot_blue))
    }

    private fun getItem(i: Int): Int {
        return binding.viewPager.getCurrentItem() + i
    }
}