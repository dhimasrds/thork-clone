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
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.server.ServerActivity
import timber.log.Timber

class IntroActivity : BaseActivity() {
    val TAG = IntroActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
    }

    fun nextPage(view:View) {
        Timber.tag(TAG).i("nextPage() view: %s", view.id)
        val preferenceManager: PreferenceManager = PreferenceManager(this)
        preferenceManager.putBoolean(BaseParam.APP_FIRST_LAUNCH, false)
        startActivity(Intent(this, ServerActivity::class.java))
        finish()
    }
}