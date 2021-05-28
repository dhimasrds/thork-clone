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
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import id.thork.app.base.BaseApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.*

object FileUtils {
    fun getMimeType(context: Context, uri: Uri): String? {
        val mimeType: String?
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

    fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    fun isImageType(mimeType: String): Boolean {
        return mimeType.startsWith("image")
    }

    fun isPdfType(mimeType: String): Boolean {
        return mimeType.equals("application/pdf")
    }

    fun isWordType(mimeType: String): Boolean {
        return mimeType.endsWith("doc") || mimeType.endsWith("docx")
    }

    fun isExcelType(mimeType: String): Boolean {
        return mimeType.endsWith("xls") || mimeType.endsWith("xlsx")
    }

    fun getFilename(context: Context, uri: Uri): String? {
        var fileName: String? = null
        try {
            val schema = uri.scheme
            Timber.tag(BaseApplication.TAG).d("getFileName() schema: %s uri: %s", schema, uri)
            if (schema.equals("file")) {
                fileName = uri.lastPathSegment
            } else if (!schema.isNullOrEmpty() && schema.startsWith("http")) {
                fileName = uri.lastPathSegment
            } else if (schema.equals("content")) {
                val returnCursor = context.contentResolver.query(uri, null, null, null, null)
                val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor?.moveToFirst()
                fileName = nameIndex?.let { returnCursor.getString(it) }
                returnCursor?.close()
            }
            Timber.tag(BaseApplication.TAG).d("getFileName() fileName: %s", fileName)
        } catch (e: Exception) {
            Timber.tag(BaseApplication.TAG).d("getFileName() error: %s", e)
        }
        return fileName
    }

    @Throws(IOException::class)
    fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }

    fun createTempFileFromContentUri(context: Context, contentUri: Uri): File {
        // Preparing Temp file name
        val fileExtension = FileUtils.getFileExtension(context, contentUri)
        //val fileName = "tmp" + if (fileExtension != null) ".$fileExtension" else ""
        val fileName = getFilename(context, contentUri)

        // Creating Temp file
        Timber.tag(BaseApplication.TAG).d(
            "createTempFileFromContentUri() content uri: %s context: %s fileName: %s",
            contentUri, context.cacheDir, fileName
        )
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()

        try {
            val oStream = FileOutputStream(tempFile)
            val inputStream = context.contentResolver.openInputStream(contentUri)
            inputStream?.let {
                FileUtils.copy(inputStream, oStream)
            }

            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tempFile
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray {
        Timber.tag(BaseApplication.TAG).d("getBytes() inputStream: %s", inputStream)
        val byteBuff = ByteArrayOutputStream()
        val buffSize = 1024
        val buff = ByteArray(buffSize)
        var len = 0
        while (inputStream.read(buff).also { len = it } != -1) {
            byteBuff.write(buff, 0, len)
        }
        return byteBuff.toByteArray()
    }

    /**
     * Create Retrofit Request Body from File URI
     */
    fun createRequestBodyFromUri(context: Context, uriString: String): RequestBody? {
        var requestFile: RequestBody? = null
        if (!uriString.isNullOrBlank()) {
//            val fileUri = Uri.parse(uriString)
            val mimeType = getMimeType(context, Uri.parse(uriString))
            val fileName = getFilename(context, Uri.parse(uriString))
            val tempFile = File(context.cacheDir, fileName)
            tempFile.createNewFile()
            try {
                val oStream = FileOutputStream(tempFile)
                val inputStream = context.contentResolver.openInputStream(Uri.parse(uriString))
                inputStream?.let {
                    copy(inputStream, oStream)
                }
                oStream.flush()
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.tag(BaseApplication.TAG).d("createRequestBodyFromUri() error: %s", e)
            }
            requestFile = tempFile.asRequestBody(mimeType?.toMediaTypeOrNull())
            Timber.tag(BaseApplication.TAG)
                .d("createRequestBodyFromUri() requestFile: %s", requestFile)
        }
        return requestFile
    }

}