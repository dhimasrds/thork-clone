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
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
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
    lateinit var screens: IntArray
    private lateinit var myvpAdapter: MyViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        screens = intArrayOf(
                R.layout.layout_welcome1,
                R.layout.layout_welcome2,
                R.layout.layout_welcome3
        )

        myvpAdapter = MyViewPagerAdapter(screens)
        binding.viewPager.setAdapter(myvpAdapter)
        binding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener)

        if (!preferenceManager.getBoolean(BaseParam.APP_FIRST_LAUNCH)) {
            launchMain()
            finish()
        }

        ColoredBars(0)
    }

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@IntroActivity
            vm = viewModel
        }
    }

    //    fun nextPage(view: View) {
//        Timber.tag(TAG).i("nextPage() view: %s", view.id)
//        preferenceManager.putBoolean(BaseParam.APP_FIRST_LAUNCH, false)
//        startActivity(Intent(this, ServerActivity::class.java))
//        finish()
//    }
    fun next(v: View?) {
        val i = getItem(+1)
        if (i < screens.count()) {
            binding.viewPager.currentItem = i
        } else {
            launchMain()
        }
    }

    fun skip(v: View?) {
        launchMain()
    }

    private fun ColoredBars(thisScreen: Int) {
        bottomBars = arrayOfNulls(screens.size)
        binding.layoutBars.removeAllViews()
        for (i in bottomBars.indices) {
            bottomBars[i] = ImageView(this)
            bottomBars.get(i)!!.setImageDrawable(resources.getDrawable(R.drawable.ic_dot))
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(10, 10, 10, 10)
            bottomBars.get(i)?.setLayoutParams(lp)
            binding.layoutBars.addView(bottomBars.get(i))
        }
        if (bottomBars.size > 0) bottomBars.get(thisScreen)?.setImageDrawable(resources.getDrawable(R.drawable.ic_dot_blue))
    }

    private fun getItem(i: Int): Int {
        return binding.viewPager.getCurrentItem() + i
    }

    private fun launchMain() {
        preferenceManager.putBoolean(BaseParam.APP_FIRST_LAUNCH, false)
        startActivity(Intent(this@IntroActivity, ServerActivity::class.java))
        finish()
    }

    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            ColoredBars(position)
            if (position == screens.size - 1) {
                binding.next.text = "start"
                binding.skip.visibility = View.GONE
            } else {
                binding.next.text = getString(R.string.next)
                binding.skip.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }
}