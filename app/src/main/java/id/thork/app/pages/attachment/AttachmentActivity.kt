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
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
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
import com.skydoves.whatif.whatIf
import com.skydoves.whatif.whatIfNotNullOrEmpty
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityAttachmentBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.attachment.element.AttachmentAdapter
import id.thork.app.pages.attachment.element.AttachmentViewModel
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.utils.FileUtils
import timber.log.Timber
import java.util.*
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

    private var intentWoId = 0

    @Inject
    @Named("svgRequestOption")
    lateinit var svgRequestOptions: RequestOptions

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun setupView() {
        super.setupView()
        attachmentEntities = mutableListOf()
        attachmentAdapter =
            AttachmentAdapter(this, preferenceManager, svgRequestOptions, this, attachmentEntities)

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
            option = true,
            historyAttendanceIcon = false
        )

        pickiT = PickiT(this, this, this)
        requestPermission()

        setupDialog()

        retrieveFromIntent()
        viewModel.fetchAttachments(intentWoId)
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnUpload.setOnClickListener {
            Timber.tag(TAG).d("setupListener() upload start")
            viewModel.uploadAttachment()
        }
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
        Timber.tag(TAG).d(
            "onActivityResult() request code: %s result code: %s data: %s",
            requestCode, resultCode, data
        )
        when (requestCode) {
            SELECT_DOCUMENT_REQUEST -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val uri: Uri = data?.data!!
                    Timber.tag(TAG).d("onActivityResult() file picker data: %s", uri.toString())
                    navigateToPreview(uri)
                }
            }
            SELECT_SIGNATURE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val uri: Uri = data?.data!!
                    navigateToPreview(uri)
                    Timber.tag(TAG).d("onActivityResult() signature data: %s", uri.toString())
                }
            }
            else -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri: Uri = data?.data!!
                    Timber.tag(TAG).d("onActivityResult() camera uri: %s", uri.toString())
                    navigateToPreview(uri)
                } else if (resultCode == ImagePicker.REQUEST_CODE) {
                    val uri: Uri = data?.data!!
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

    private fun retrieveFromIntent() {
        intentWoId = intent.getIntExtra(BaseParam.WORKORDERID, 0)
        Timber.d("retrieveFromIntent() intentWoId: %s", intentWoId)
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
        dialogUtils.isCancelable = true
        dialogUtils.setInflater(R.layout.layout_attachment_preview, null, li).create()

    }

    private fun navigateToPreview(uri: Uri) {
        val mimeType = FileUtils.getMimeType(this, uri)
        val fileName = FileUtils.getFilename(this, uri)
        Timber.tag(TAG).d("navigateToPreview() uri: %s type: %s", uri, mimeType)
        setupDialog()
        dialogUtils.show()

        val layoutDocPreview = dialogUtils.setViewId(R.id.layout_doc_preview) as LinearLayout
        val ivPreview = dialogUtils.setViewId(R.id.iv_preview) as ImageView
        val etAttachmentCaption = dialogUtils.setViewId(R.id.et_attachment_caption) as EditText
        mimeType.whatIfNotNullOrEmpty {
            whatIf(FileUtils.isImageType(it),
                whatIf = {
                    Timber.tag(TAG).d("navigateToPreview() is Image: true")
                    layoutDocPreview.visibility = View.INVISIBLE
                    ivPreview.visibility = View.VISIBLE
                    ivPreview.setImageURI(uri)
                },
                whatIfNot = {
                    Timber.tag(TAG).d("navigateToPreview() is Document: true")
                    layoutDocPreview.visibility = View.VISIBLE
                    ivPreview.visibility = View.INVISIBLE
                    val tvdocName = dialogUtils.setViewId(R.id.tv_doc_preview) as TextView
                    tvdocName.text = fileName
                })
        }

        val toolBarAttachment: Toolbar = dialogUtils.setViewId(R.id.app_toolbar) as Toolbar
        toolBarAttachment.setNavigationIcon(R.drawable.ic_arrow_back_white)
        toolBarAttachment.setNavigationOnClickListener {
            Timber.tag(TAG).d("navigateToPreview() toolbar event: true")
            dialogUtils.dismiss()
        }

        val fabSave = dialogUtils.setViewId(R.id.fab_save) as FloatingActionButton
        setupFabButton(fabSave)
        fabSave.setOnClickListener {
            Timber.tag(TAG).d("navigateToPreview() fileName: %s mimeType: %s", fileName, mimeType)
            if (fileName != null && !fileName.isEmpty() &&
                mimeType != null && !mimeType.isEmpty()
            ) {
                addAttachment(
                    WoCacheEntity(woId = intentWoId),
                    uri.toString(),
                    etAttachmentCaption.text.toString(),
                    fileName,
                    mimeType
                )
                viewModel.uploadAttachment()
            }
            dialogUtils.dismiss()
        }
    }

    @Suppress("DEPRECATION")
    private fun setupFabButton(fab: FloatingActionButton) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fab.colorFilter =
                BlendModeColorFilter(Color.parseColor("#FFFFFFFF"), BlendMode.SRC_ATOP)
        } else {
            fab.setColorFilter(Color.parseColor("#FFFFFFFF"), PorterDuff.Mode.SRC_ATOP);
        }
    }

    override fun PickiTonUriReturned() {
    }

    override fun PickiTonStartListener() {
    }

    override fun PickiTonProgressUpdate(progress: Int) {
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

    private fun addAttachment(
        woCacheEntity: WoCacheEntity,
        uriString: String,
        description: String,
        name: String,
        mimeType: String
    ) {
        var docType: String? = BaseParam.ATTACHMENT_FOLDER

        val attachmentEntity = AttachmentEntity(
            mimeType = mimeType, syncStatus = false,
            uriString = uriString, description = description,
            title = name, workOrderId = woCacheEntity.woId,
            fileName = name, docType = docType,
            wonum = woCacheEntity.wonum, modifiedDate = Date()
        )
        viewModel.addItem(attachmentEntity)
    }


}