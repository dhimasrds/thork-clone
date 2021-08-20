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

package id.thork.app.base

import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.github.dhaval2404.imagepicker.ImagePicker
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.ConnectionLiveData
import id.thork.app.di.module.ResourceProvider
import id.thork.app.helper.ConnectionState
import id.thork.app.helper.CookieHelper
import id.thork.app.network.GlideApp
import id.thork.app.pages.attachment.element.signature.SignatureActivity
import id.thork.app.pages.followup_wo.FollowUpWoActivity
import id.thork.app.persistence.dao.AttendanceDao
import id.thork.app.persistence.dao.TaskDao
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.utils.CommonUtils
import id.thork.app.workmanager.WorkerCoordinator
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {
    protected inline fun <reified T : ViewDataBinding> binding(
        @LayoutRes resId: Int,
    ): Lazy<T> = lazy { DataBindingUtil.setContentView<T>(this, resId) }

    protected val SELECT_DOCUMENT_REQUEST = 555
    protected val SELECT_SIGNATURE_REQUEST = 556

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    @Inject
    lateinit var resourceProvider: ResourceProvider

    @Inject
    lateinit var workerCoordinator: WorkerCoordinator

    @Inject
    lateinit var woCacheDao: WoCacheDao

    @Inject
    lateinit var attendanceDao: AttendanceDao

    @Inject
    lateinit var taskDao: TaskDao

    @Inject
    lateinit var appSession: AppSession

    var isConnected = false

    var mainView: ViewGroup? = null

    lateinit var toolBar: Toolbar
    private var optionMenu: Menu? = null
    private var filterIcon: Boolean = false
    private var scannerIcon: Boolean = false
    private var notificationIcon: Boolean = false
    private var optionIcon: Boolean = false
    private var followUpWoIcon: Boolean = false
    private var historyAttendanceIcon: Boolean = false
    private var originWo: String? = null
    private var cookie: String? = null
    private var connStat: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCookie()
        setupView()
        setupListener()
        setupObserver()

        Timber.tag(BaseApplication.TAG).i("onCreate() coordinator instance: %s", workerCoordinator)
        workerCoordinator.ping()
    }

    fun setupCookie() {
        runBlocking {
            appSession.userEntity.whatIfNotNull { userEntity ->
                userEntity.userHash.whatIfNotNull { userHash ->
                    cookie =
                        CookieHelper(BaseApplication.context, userHash).generateCookieIfExpired()
                    appSession.cookie = cookie
                }
            }
            Timber.d("setupCookie() cookie :%s ", appSession.cookie)
        }
    }

    open fun setupToolbarWithHomeNavigation(
        title: String,
        navigation: Boolean,
        filter: Boolean,
        scannerIcon: Boolean,
        notification: Boolean,
        option: Boolean,
        historyAttendanceIcon: Boolean
    ) {
        val imageUrl: String? = appSession.userEntity.imageLibRef
        toolBar = findViewById(R.id.app_toolbar)
        val toolBarTitle: TextView = findViewById(R.id.toolbar_title)
        val editTextToolbar: EditText = findViewById(R.id.toolbartext)
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolBarTitle.text = title
        toolBarTitle.visibility = View.GONE

        if (navigation) {
            val profile: ImageView = findViewById(R.id.profile_image)
            if (cookie != null && imageUrl != null) {
                setupProfilePicture(imageUrl, cookie, profile)
            }
            profile.visibility = View.VISIBLE
            profile.setOnClickListener {
                goToSettingsActivity()
            }
            toolBar.setNavigationOnClickListener {
            }
            toolBar.inflateMenu(R.menu.filter_menu)
        } else {
            editTextToolbar.visibility = View.GONE
            toolBarTitle.visibility = View.VISIBLE
            if (toolBarTitle.text == getString(R.string.action_profile)) {
                toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white)
            } else {
                toolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            }
            toolBar.setNavigationOnClickListener {
                goToPreviousActivity()
            }
        }

        if (filter) {
            filterIcon = filter
        }

        if (scannerIcon) {
            this.scannerIcon = scannerIcon
        }

        if (notification) {
            this.notificationIcon = notification
        }

        if (option) {
            this.optionIcon = option
        }

        if (historyAttendanceIcon) {
            this.historyAttendanceIcon = historyAttendanceIcon
        }

        setupToolbarOverflowIcon()
    }

    private fun setupProfilePicture(
        imageUri: String?,
        cookie: String?,
        imageView: ImageView
    ) {
        imageUri.whatIfNotNull {
            cookie.whatIfNotNullOrEmpty { cookie ->
                val glideUrl = GlideUrl(
                    it, LazyHeaders.Builder()
                        .addHeader("Cookie", cookie)
                        .build()
                )
                GlideApp.with(BaseApplication.context).load(glideUrl)
                    .circleCrop()
                    .into(imageView)
            }
        }
    }

    open fun enableFollowUpWo(enable: Boolean, wonum: String) {
        if (enable) {
            this.followUpWoIcon = enable
            originWo = wonum
        }
    }

    @Suppress("DEPRECATION")
    private fun setupToolbarOverflowIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            toolBar.overflowIcon?.colorFilter =
                BlendModeColorFilter(Color.parseColor("#AEAEAE"), BlendMode.SRC_ATOP)
        } else {
            toolBar.overflowIcon?.setColorFilter(
                Color.parseColor("#AEAEAE"),
                PorterDuff.Mode.SRC_ATOP
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Timber.tag(BaseApplication.TAG).i("onLostConnection() connected menu")
        optionMenu = menu
        menuInflater.inflate(R.menu.actionbar_menu, optionMenu)
        optionMenu?.findItem(R.id.action_filter)?.isVisible = filterIcon
        optionMenu?.findItem(R.id.scan_menu)?.isVisible = scannerIcon
        optionMenu?.findItem(R.id.action_notif)?.isVisible = notificationIcon

        optionMenu?.findItem(R.id.action_capture_image)?.isVisible = optionIcon
        optionMenu?.findItem(R.id.action_attach_image)?.isVisible = optionIcon
        optionMenu?.findItem(R.id.action_attach_document)?.isVisible = optionIcon
        optionMenu?.findItem(R.id.action_attach_signature)?.isVisible = optionIcon

        optionMenu?.findItem(R.id.action_create_followup)?.isVisible = followUpWoIcon
        optionMenu?.findItem(R.id.history_attendance)?.isVisible = historyAttendanceIcon
        defineIconConnectionState()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_conn) {
            Timber.tag(BaseApplication.TAG).i("onOptionsItemSelected() action conn")
            return true
        }

        if (id == R.id.scan_menu) {
            Timber.tag(BaseApplication.TAG).i("onOptionsItemSelected() action scan menu")
            gotoScannerActivity()
            return true
        }

        if (id == R.id.action_filter) {
            Timber.tag(BaseApplication.TAG).i("onOptionsItemSelected() action filter")
            return true
        }

        if (id == R.id.action_notif) {
            Timber.tag(BaseApplication.TAG).i("onOptionsItemSelected() action filter")
            return true
        }

        if (id == R.id.action_capture_image) {
            Timber.tag(BaseApplication.TAG).i("onOptionsItemSelected() action capture image")
            ImagePicker.with(this)
                .cameraOnly()
                .start()
            return true
        }

        if (id == R.id.action_attach_image) {
            Timber.tag(BaseApplication.TAG).i("onOptionsItemSelected() action attach image")
            openGallery()
            return true
        }

        if (id == R.id.action_attach_document) {
            Timber.tag(BaseApplication.TAG).i("onOptionsItemSelected() action attach document")
            openDocuments()
            return true
        }
        if (id == R.id.action_attach_signature) {
            Timber.tag(BaseApplication.TAG).i("onOptionsItemSelected() action attach signature")
            openSignature()
            return true
        }
        if (id == R.id.action_create_followup) {
            Timber.tag(BaseApplication.TAG).i("onOptionsItemSelected() action create follow up")
            createFollowUp()
            return true
        }

        if (id == R.id.history_attendance) {
            Timber.tag(BaseApplication.TAG).i("onOptionsItemSelected() history attendance")
            gotoHistoryAttendanceActivity()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openGallery() {
        ImagePicker.with(this)
            .galleryOnly()
            .start()
    }

    private fun openDocuments() {
        val intent: Intent
        intent = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        } else {
            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI)
        }
        intent.type = "application/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.putExtra("return-data", true)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, SELECT_DOCUMENT_REQUEST)
    }

    private fun openSignature() {
        val intent = Intent(this, SignatureActivity::class.java)
        startActivityForResult(intent, SELECT_SIGNATURE_REQUEST)
    }

    private fun createFollowUp() {
        val intent = Intent(this, FollowUpWoActivity::class.java)
        intent.putExtra(BaseParam.WONUM, originWo)
        startActivity(intent)
        finish()
    }

    open fun setupMainView(mainView: ViewGroup) {
        this.mainView = mainView
    }

    open fun setupView() {
    }

    open fun setupListener() {
    }

    open fun setupObserver() {
        Timber.tag(BaseApplication.TAG)
        connectionLiveData.observe(this, { connectionState ->
            isConnected.let {
                Timber.tag(BaseApplication.TAG)
                    .i("setupObserver() connection state: %s", connectionState)
                defineConnectionState(connectionState)
            }
        })
    }

    open fun onSuccess() {
    }

    open fun onError() {
    }

    open fun showToast() {
    }

    open fun onGoodConnection() {
//        Timber.tag(BaseApplication.TAG).i("onGoodConnection() connected")
//        connStat = BaseParam.GOOD_CONNECTION
        appSession.connectionState = BaseParam.GOOD_CONNECTION
        //TODO sync update status Workorder when online
        val woCacheList =
            woCacheDao.findWoListBySyncStatusAndisChange(BaseParam.APP_FALSE, BaseParam.APP_TRUE)
        Timber.tag(BaseApplication.TAG)
            .i("onGoodConnection() size local cache %s", woCacheList.size)
        woCacheList.whatIfNotNullOrEmpty {
            workerCoordinator.addSyncWoQueue()
        }

        val attendance = attendanceDao.findAttendanceByOfflinemode(BaseParam.APP_TRUE)
        attendance.whatIfNotNull {
            workerCoordinator.addSyncAttendance()
        }

        val task = taskDao.findTaskListByOfflineModeAndIsFromWoDetail(
            BaseParam.APP_TRUE,
            BaseParam.APP_TRUE
        )
        task.whatIfNotNullOrEmpty {
            workerCoordinator.addSyncTask()
        }
        defineIconConnectionState()
    }

    open fun onSlowConnection() {
//        connStat = BaseParam.SLOW_CONNECTION
        appSession.connectionState = BaseParam.SLOW_CONNECTION
        CommonUtils.warningToast(resourceProvider.getString(R.string.connection_slow))
        //TODO sync update status Workorder when online
        val woCacheList =
            woCacheDao.findWoListBySyncStatusAndisChange(BaseParam.APP_FALSE, BaseParam.APP_TRUE)
        Timber.tag(BaseApplication.TAG)
            .i("onSlowConnection() size local cache %s", woCacheList.size)
        woCacheList.whatIfNotNullOrEmpty {
            workerCoordinator.addSyncWoQueue()
        }

        val attendance = attendanceDao.findAttendanceByOfflinemode(BaseParam.APP_TRUE)
        attendance.whatIfNotNull {
            workerCoordinator.addSyncAttendance()
        }

        val task = taskDao.findTaskListByOfflineModeAndIsFromWoDetail(
            BaseParam.APP_TRUE,
            BaseParam.APP_TRUE
        )
        task.whatIfNotNullOrEmpty {
            workerCoordinator.addSyncTask()
        }
        defineIconConnectionState()
    }

    open fun onLostConnection() {
//        connStat = BaseParam.LOST_CONNECTION
        appSession.connectionState = BaseParam.LOST_CONNECTION
        Timber.tag(BaseApplication.TAG).i("onLostConnection() connected")
        CommonUtils.errorToast(resourceProvider.getString(R.string.connection_not_available))
        Timber.tag(BaseApplication.TAG).i("onLostConnection() option menu :%s", optionMenu)
        defineIconConnectionState()
    }

    open fun onNotificationReceived(message: String) {
        Timber.tag(BaseApplication.TAG).i("onNotificationReceived")
    }

    private fun defineIconConnectionState() {
        optionMenu.whatIfNotNull {
            when (appSession.connectionState) {
                BaseParam.GOOD_CONNECTION -> {
                    optionMenu?.findItem(R.id.action_conn)?.setIcon(R.drawable.ic_conn_on)

                }
                BaseParam.SLOW_CONNECTION -> {
                    optionMenu?.findItem(R.id.action_conn)?.setIcon(R.drawable.ic_conn_slow)
                }
                BaseParam.LOST_CONNECTION -> {
                    optionMenu?.findItem(R.id.action_conn)?.setIcon(R.drawable.ic_conn_off)
                }
            }
        }
    }

    private fun defineConnectionState(connectionState: Int) {
        Timber.tag(BaseApplication.TAG).i("defineConnectionState :%s", connectionState)
        if (connectionState >= ConnectionState.SLOW.state) {
            onConnection(true)
            if (connectionState.equals(ConnectionState.SLOW.state)) {
                onSlowConnection()
            } else {
                onGoodConnection()
            }
            Timber.tag(BaseApplication.TAG).i(
                "defineConnectionState() connectionState: %s",
                connectionState
            )

        } else {
            onConnection(false)
            onLostConnection()
        }
    }

    private fun onConnection(isConnected: Boolean) {
        this.isConnected = isConnected
    }

    open fun gotoScannerActivity() {

    }

    open fun gotoHistoryAttendanceActivity() {

    }

    open fun goToSettingsActivity() {

    }

    open fun goToPreviousActivity() {
        finish()
    }
}