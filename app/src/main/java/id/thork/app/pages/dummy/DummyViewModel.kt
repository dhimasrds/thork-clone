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

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.BaseApplication
import id.thork.app.base.LiveCoroutinesViewModel
import timber.log.Timber

class DummyViewModel: LiveCoroutinesViewModel() {
    val _progressVisible = MutableLiveData<Boolean>(false)
    val progressVisible: LiveData<Boolean> get() = _progressVisible

    fun validate() {
        Timber.tag(BaseApplication.TAG).i("validate")
        _progressVisible.value = true
        val handler: Handler = Handler()
        handler.postDelayed({
            _progressVisible.value = false
        }, 4000)
    }
}