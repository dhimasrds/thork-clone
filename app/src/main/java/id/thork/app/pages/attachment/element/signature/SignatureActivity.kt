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
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore.Images
import com.github.gcacace.signaturepad.views.SignaturePad
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivitySignatureBinding
import timber.log.Timber
import java.io.ByteArrayOutputStream


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
                val uri = getSignatureUri(this, it.signatureBitmap, "signatureReja ".plus(System.currentTimeMillis()))
                uri.whatIfNotNull {
                    Timber.tag(TAG).d("btnSaveListener() uri: %s path: %s", it, it.path)
                }
            }
        }
    }

    fun getSignatureUri(context: Context, bitmap: Bitmap, title: String): Uri? {
        Timber.tag(TAG).d("getSignatureUri() bitmap: %s title: %s", bitmap, title)
        try {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            val values = ContentValues()
            values.put(Images.Media.TITLE, title)
            values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(Images.Media.BUCKET_DISPLAY_NAME, title)
            values.put(Images.Media.MIME_TYPE, "image/jpeg");
            val uri = context.contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values)
            return uri
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
//
//        val relativeLocation = Environment.DIRECTORY_PICTURES + File.pathSeparator + title
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
//                put(MediaStore.MediaColumns.IS_PENDING, 1)
//            }
//        }
//
//        val resolver = SignatureActivity().contentResolver
//        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//        return uri
    }
}