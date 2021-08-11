/*
 * *
 *  * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved. <BR>
 *  * <BR>
 *  * This software is the confidential and proprietary information of
 *  * This.ID. ("Confidential Information").<BR>
 *  * <BR>
 *  * Such Confidential Information shall not be disclosed and shall
 *  * use it only	 in accordance with the terms of the license agreement
 *  * entered into with This.ID; other than in accordance with the written
 *  * permission of This.ID. <BR>
 *  *
 *
 *
 */
package id.thork.app.utils

import android.content.Context
import android.os.Build
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import java.io.File
import java.util.*

/**
 * Created by Reja on 10,August,2021
 * Jakarta, Indonesia.
 */
object FormUtils {
    fun isFormReadOnly(woStatus: String): Boolean {
        val match = BaseParam.FORM_STATE_WO_READ_ONLY.filter {
            woStatus.equals(it, ignoreCase = false)
        }
        return match.size > 0
    }
}