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

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import id.thork.app.BuildConfig
import id.thork.app.base.BaseApplication
import timber.log.Timber


object IntentUtils {
    fun displayData(context: Context, uriString: String) {
        if (uriString.startsWith("http")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Timber.tag(BaseApplication.TAG).d("displayData() error: %s", e)
            }
        } else {
            var uri: Uri = Uri.parse(uriString)
            if (uri.toString().contains("raw")) {
                var newUriString = Uri.decode(uri.toString())
                if (newUriString.contains("raw:")) {
                    val index = newUriString.indexOf("raw:", 0, true) + ("raw:".length)
                    newUriString = "file://".plus(newUriString.substring(index))
                }
                uri = Uri.parse(newUriString)
            }
            val intent = Intent(Intent.ACTION_VIEW)
            val tmpUri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                FileUtils.createTempFileFromContentUri(context, uri)
            )
            Timber.tag(BaseApplication.TAG).d("displayData() oldUri: %s newUri: %s", uri, tmpUri)
            context.grantUriPermission(
                context.packageName, tmpUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            )
            intent.setDataAndType(tmpUri, FileUtils.getMimeType(context, tmpUri))

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        }
    }
}