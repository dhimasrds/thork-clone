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

package id.thork.app.pages.example

import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.GlideApp
import javax.inject.Inject

@AndroidEntryPoint
class ImageActivity : BaseActivity() {

    @Inject
    lateinit var requestOptions: RequestOptions

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        loadImageHttps()
    }

    private fun loadImageHttps() {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val imageView: ImageView = findViewById(R.id.image_view)
        val imageUrl =
            "https://fsm.this.id/maximo/oslc/os/thisfsmwodetail/_R1NULzEwMjI-/doclinks/87"
        val glideUrl = GlideUrl(
            imageUrl, LazyHeaders.Builder()
                .addHeader("Cookie", cookie)
                .build()
        )
        GlideApp.with(this).load(glideUrl)
            .apply(requestOptions)
            .into(imageView)
    }

}