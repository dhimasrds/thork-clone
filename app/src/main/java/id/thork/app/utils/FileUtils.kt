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

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap

object FileUtils {
    fun getMimeType(context: Context, uri: Uri): String? {
        var mimeType: String?
        if (ContentResolver.SCHEME_CONTENT.equals(uri.scheme)) {
            val contentResolver = context.contentResolver
            mimeType = contentResolver.getType(uri)
        } else {
            val fileExtension: String = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            mimeType =
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase())
        }
        return mimeType
    }

    fun isImageType(mimeType: String): Boolean {
        return mimeType != null && mimeType.startsWith("image")
    }

    fun isPdfType(mimeType: String): Boolean {
        return mimeType != null && mimeType.equals("application/pdf")
    }

    fun isWordType(mimeType: String): Boolean {
        return mimeType != null && (mimeType.endsWith("doc") || mimeType.endsWith("docx"))
    }

    fun isExcelType(mimeType: String): Boolean {
        return mimeType != null && (mimeType.endsWith("xls") || mimeType.endsWith("xlsx"))
    }

}