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


import android.util.Base64
import android.widget.Toast
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam

object CommonUtils {
    fun isTrue(input: Int): Boolean {
        if (input.equals(BaseParam.APP_TRUE)) {
            return true
        }

        return false
    }

    fun encodeToBase64(originalText:String): String {
        return Base64.encodeToString(originalText.toByteArray(), Base64.NO_WRAP);
    }

    fun showToast(message:String) {
        Toast.makeText(BaseApplication.getAppContext(), message, Toast.LENGTH_SHORT).show()
    }


}