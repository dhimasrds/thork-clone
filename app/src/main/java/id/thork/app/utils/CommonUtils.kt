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

package id.thork.app.utils


import android.os.Handler
import android.util.Base64
import android.view.Gravity
import android.widget.Toast
import es.dmoral.toasty.Toasty
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import timber.log.Timber
import java.util.*
import kotlin.concurrent.timerTask

object CommonUtils {
    const val POSITION_CENTER = 1
    private var toast: Toast? = null
    private const val TOAST_TIMEOUT_MS: Long = 1000


    private var lastToastTime: Long = 0

    fun isTrue(input: Int): Boolean {
        if (input.equals(BaseParam.APP_TRUE)) {
            return true
        }

        return false
    }

    fun encodeToBase64(originalText: String): String {
        return Base64.encodeToString(originalText.toByteArray(), Base64.NO_WRAP);
    }

    fun showToast(message: String) {
        Toast.makeText(BaseApplication.getAppContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun showToast(message: String, position: Int) {
        val toast = Toast.makeText(BaseApplication.getAppContext(), message, Toast.LENGTH_SHORT)
        if (position == POSITION_CENTER) {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.show()
    }

    fun errorToast(message: String) {
        val toast = Toasty.error(
            BaseApplication.getAppContext(), message, Toast.LENGTH_LONG, true
        )
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    fun warningToast(message: String) {
        val toast = Toasty.warning(
            BaseApplication.getAppContext(), message, Toast.LENGTH_LONG, true
        )
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    fun standardToast(message: String) {
        Timber.d("standardToast toast :%s", toast)

        val now = System.currentTimeMillis();
        if (lastToastTime + TOAST_TIMEOUT_MS < now) {
            toast = Toast.makeText(
                BaseApplication.getAppContext(), message, Toast.LENGTH_SHORT
            )
            toast?.setGravity(Gravity.CENTER, 0, 100)
            toast?.show()
            lastToastTime = now;
        }
        Timer().schedule(timerTask {
            toast?.cancel()
        }, 500)

    }



    fun removeToast(){
        Timber.d("removeToast remove :%s", toast)
        if (toast != null){
            toast?.cancel()

        }
    }
}
