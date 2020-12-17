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

package id.thork.app.initializer

import android.util.Log
import timber.log.Timber

class ReleaseTree : Timber.Tree() {
    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return when (priority) {
            Log.VERBOSE, Log.DEBUG -> false
            else -> //Only Log Warn, Error, WTF
                true
        }
    }

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?) {
        if (isLoggable(tag, priority)) {
            // Message is short enough, doesn't need to be broken into chunks
            if (message.length < MAX_LOG_LENGTH) {
//                Crashlytics.log(priority, tag, message);
                return
            }
            var i = 0
            val length = message.length
            while (i < length) {
                var newline = message.indexOf('\n', i)
                newline = if (newline != -1) newline else length
                do {
                    val end =
                        Math.min(newline, i + MAX_LOG_LENGTH)
                    val part = message.substring(i, end)
                    //                    Crashlytics.log(priority, tag, part);
                    i = end
                } while (i < newline)
                i++
            }
        }
    }

    companion object {
        private const val MAX_LOG_LENGTH = 4000
    }
}