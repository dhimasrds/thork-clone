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

package id.thork.app.pages.attachment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityAttachmentBinding
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.attachment.element.AttachmentAdapter
import id.thork.app.pages.attachment.element.AttachmentViewModel
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.utils.FileUtils
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AttachmentActivity : BaseActivity(), PickiTCallbacks {
    val TAG = AttachmentActivity::class.java.name

    val viewModel: AttachmentViewModel by viewModels()
    private val binding: ActivityAttachmentBinding by binding(R.layout.activity_attachment)

    private lateinit var attachmentAdapter: AttachmentAdapter
    private lateinit var attachmentEntities: MutableList<AttachmentEntity>

    private lateinit var pickiT: PickiT
    private lateinit var dialogUtils: DialogUtils

    @Inject
    @Named("svgRequestOption")
    lateinit var svgRequestOptions: RequestOptions

    override fun setupView() {
        super.setupView()
        attachmentEntities = mutableListOf()
        attachmentAdapter = AttachmentAdapter(this, svgRequestOptions, this, attachmentEntities)

        binding.apply {
            lifecycleOwner = this@AttachmentActivity
            vm = viewModel

            rvAttachments.apply {
                layoutManager = LinearLayoutManager(this@AttachmentActivity)
                addItemDecoration(
                    DividerItemDecoration(
                        this@AttachmentActivity,
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = attachmentAdapter
            }
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.attachments),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = true
        )

        viewModel.fetchAttachments(1)

        pickiT = PickiT(this, this, this)
        requestPermission()

        setupDialog()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.attachments.observe(this, {
            attachmentEntities.clear()
            attachmentEntities.addAll(it)
            attachmentAdapter.notifyDataSetChanged()
            Timber.tag(TAG).d("setupObserver() size: %s", attachmentEntities.size)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_DOCUMENT_REQUEST -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val uri: Uri = data?.data!!
                    Timber.tag(TAG).d("onActivityResult() file picker data: %s", uri.toString())
                    navigateToPreview(uri)
                }
            }
            else -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri: Uri = data?.data!!
                    binding.ivThumbnail.setImageURI(uri)
                    Timber.tag(TAG).d("onActivityResult() camera uri: %s", uri.toString())
                    navigateToPreview(uri)
                } else if (resultCode == ImagePicker.REQUEST_CODE) {
                    val uri: Uri = data?.data!!
                    binding.ivThumbnail.setImageURI(uri)
                    Timber.tag(TAG).d("onActivityResult() gallery uri: %s", uri.toString())
                    navigateToPreview(uri)
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Timber.tag(TAG).d("onActivityResult() error: %s", ImagePicker.getError(data))
                } else {
                    Timber.tag(TAG).d("onActivityResult() cancel")
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun requestPermission() {
        Timber.tag(TAG).d("requestPermission()")
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    Timber.tag(TAG).d("requestPermission() result: onPermissionGranted")
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Timber.tag(TAG).d("requestPermission() result: onPermissionDenied")
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    Timber.tag(TAG)
                        .d("requestPermission() result: onPermissionRationaleShouldBeShown")
                }
            }).check()
    }

    private fun setupDialog() {
        val li = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        dialogUtils =
            DialogUtils(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        dialogUtils.setInflater(R.layout.layout_attachment_preview, null, li)
    }

    private fun navigateToPreview(uri: Uri) {
        val mimeType = FileUtils.getMimeType(this, uri)
        Timber.tag(TAG).d("navigateToPreview() uri: %s type: %s", uri, mimeType)

        dialogUtils.create().setRounded(true)
        dialogUtils.show()
        val imageView = dialogUtils.setViewId(R.id.iv_preview) as ImageView
        imageView.setImageURI(uri)

        val fabSave = dialogUtils.setViewId(R.id.fab_save) as FloatingActionButton
        fabSave.setOnClickListener {
            Timber.tag(TAG).d("navigateToPreview() save: close")
            dialogUtils.dismiss()
        }
    }

    override fun PickiTonUriReturned() {
        TODO("Not yet implemented")
    }

    override fun PickiTonStartListener() {
        TODO("Not yet implemented")
    }

    override fun PickiTonProgressUpdate(progress: Int) {
        TODO("Not yet implemented")
    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        TODO("Not yet implemented")
    }
}