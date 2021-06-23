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

package id.thork.app.pages.attachment.element.signature

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import com.github.gcacace.signaturepad.views.SignaturePad
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivitySignatureBinding
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.IOException


class SignatureActivity : BaseActivity() {
    val TAG = SignatureActivity::class.java.name

    private val binding: ActivitySignatureBinding by binding(R.layout.activity_signature)

    var signaturePad: SignaturePad? = null

    override fun setupView() {
        super.setupView()

        binding.apply {
            lifecycleOwner = this@SignatureActivity
        }
        setupToolbarWithHomeNavigation(
            getString(R.string.attachments),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )
    }

    override fun setupListener() {
        super.setupListener()
        binding.signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
            }

            override fun onSigned() {
            }

            override fun onClear() {
                Timber.tag(TAG).d("onClear()")
            }
        })

        binding.btnClear.setOnClickListener {
            Timber.tag(TAG).d("btnClearListener()")
            binding.signaturePad.clear()
        }

        binding.btnSave.setOnClickListener {
            Timber.tag(TAG).d("btnSaveListener()")
            binding.signaturePad.whatIfNotNull {
                val uri = saveSignatureAsBitmap(this, it.signatureBitmap, Bitmap.CompressFormat.JPEG,
                    "image/jpg", "signatured-".plus(System.currentTimeMillis().toString().plus(".jpg")) )
                uri.whatIfNotNull {
                    Timber.tag(TAG).d("btnSaveListener() uri: %s path: %s", it, it.path)
                    val intent = Intent()
                    intent.data = it
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    @Throws(IOException::class)
    fun saveSignatureAsBitmap(
        context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {

        val values = ContentValues().apply {
            put(Images.Media.DISPLAY_NAME, displayName)
            put(Images.Media.MIME_TYPE, mimeType)
            put(Images.Media.DATE_TAKEN, System.currentTimeMillis())
            put(Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        val resolver = context.contentResolver
        var uri: Uri? = null

        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Failed to create new MediaStore record.")

            resolver.openOutputStream(uri)?.use {
                if (!bitmap.compress(format, 95, it))
                    throw IOException("Failed to save bitmap.")
            } ?: throw IOException("Failed to open output stream.")
            return uri

        } catch (e: IOException) {

            uri?.let { orphanUri ->
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(orphanUri, null, null)
            }

            throw e
        }
    }
}