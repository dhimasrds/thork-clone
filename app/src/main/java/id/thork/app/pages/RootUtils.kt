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
package id.thork.app.pages

import android.content.Context
import com.scottyab.rootbeer.RootBeer

/**
 * Created by Dhimas Saputra on 30/11/20
 * Jakarta, Indonesia.
 */

class RootUtils(context: Context) : RootBeer(context) {
    val isRootedWithoutTestKey: Boolean
        get() = (detectRootManagementApps() || detectPotentiallyDangerousApps()
                || checkForRWPaths() || checkForBinary("busybox")
                || checkSuExists() || checkForRootNative() || checkForMagiskBinary())
}