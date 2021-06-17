package id.thork.app.pages.profiles.attendance


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skydoves.whatif.whatIf
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseApplication.Constants.context
import id.thork.app.databinding.ActivityAttandenceBinding
import id.thork.app.network.GlideApp
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.profiles.attendance.element.AttandanceViewModel
import id.thork.app.utils.DateUtils
import id.thork.app.utils.FileUtils
import timber.log.Timber
import java.util.*

class AttendanceActivity : BaseActivity() {
    val TAG = AttendanceActivity::class.java.name
    private val viewModels: AttandanceViewModel by viewModels()
    private val binding: ActivityAttandenceBinding by binding(R.layout.activity_attandence)

    private lateinit var dialogUtils: DialogUtils
    private lateinit var locationManager: LocationManager
    private var latitudey: Double? = null
    private var longitudex: Double? = null
    private var currentTimeMillSec: Long? = null
    private var isCheckIn: Boolean? = null


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@AttendanceActivity
            vm = viewModels
        }

        locationManager = (getSystemService(LOCATION_SERVICE) as LocationManager)

        setupToolbarWithHomeNavigation(
            getString(R.string.attendance),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )

        setupCurrentTimeMillSec()
        setupDialog()
        pickLocation()
        isCheckin()
    }

    override fun setupListener() {
        super.setupListener()
        binding.ivCaptureImage.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .start()
        }

        binding.btnSaveAttendance.setOnClickListener {
            //TODO save objectbox
        }
    }

    private fun isCheckin() {
//        val checkIn = viewModels.findCheckInAttendance()
//        Timber.d("raka %s", checkIn)
//        if (checkIn == null){
//            isCheckIn = true
//            binding.tvDateCheckIn.text = DateUtils.getDateTimeCardView(currentTimeMillSec!!)
//            binding.tvCheckIn.text = DateUtils.getCheckAttendance(currentTimeMillSec!!)
//            binding.tvDate.text = DateUtils.getDateTimeHeaderAttendance()
//        } else {
//            isCheckIn = false
//            binding.tvDateCheckIn.text = checkIn.dateCheckIn
//            binding.tvCheckIn.text = checkIn.hoursCheckIn
//            binding.tvDateCheckOut.text = DateUtils.getDateTimeCardView(currentTimeMillSec!!)
//            binding.tvCheckOut.text = DateUtils.getCheckAttendance(currentTimeMillSec!!)
//            binding.tvDate.text = checkIn.dateTimeHeader
//        }
//        Timber.d("raka %s", isCheckIn)
    }

    private fun setupCurrentTimeMillSec() {
        currentTimeMillSec = DateUtils.getCurrentTimeMillisec()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.tag(TAG).d(
            "onActivityResult() request code: %s result code: %s data: %s",
            requestCode, resultCode, data
        )
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri: Uri = data?.data!!
                Timber.tag(TAG).d("onActivityResult() camera uri: %s", uri.toString())
                navigateToPreview(uri)
            }
            ImagePicker.REQUEST_CODE -> {
                val uri: Uri = data?.data!!
                Timber.tag(TAG).d("onActivityResult() gallery uri: %s", uri.toString())
                navigateToPreview(uri)
            }
            ImagePicker.RESULT_ERROR -> {
                Timber.tag(TAG).d("onActivityResult() error: %s", ImagePicker.getError(data))
            }
            else -> {
                Timber.tag(TAG).d("onActivityResult() cancel")
            }
        }
    }

    private fun navigateToPreview(uri: Uri) {
        val mimeType = FileUtils.getMimeType(this, uri)
        val fileName = FileUtils.getFilename(this, uri)
        Timber.tag(TAG)
            .d("navigateToPreview() uri: %s type: %s filename: %s", uri, mimeType, fileName)
        setupDialog()
        dialogUtils.show()

        val layoutDocPreview = dialogUtils.setViewId(R.id.layout_doc_preview) as LinearLayout
        val ivPreview = dialogUtils.setViewId(R.id.iv_preview) as ImageView
        mimeType.whatIfNotNullOrEmpty {
            whatIf(FileUtils.isImageType(it),
                whatIf = {
                    Timber.tag(TAG).d("navigateToPreview() is Image: true")
                    layoutDocPreview.visibility = View.INVISIBLE
                    ivPreview.visibility = View.VISIBLE
                    ivPreview.setImageURI(uri)
                })
        }

        val fabSave = dialogUtils.setViewId(R.id.fab_save) as FloatingActionButton
        val etDesc = dialogUtils.setViewId(R.id.et_attachment_caption) as EditText
        setupFabButton(fabSave)
        etDesc.visibility = View.INVISIBLE
        fabSave.setOnClickListener {
            Timber.tag(TAG).d("navigateToPreview() fileName: %s mimeType: %s", fileName, mimeType)
            if (fileName != null && fileName.isNotEmpty() &&
                mimeType != null && mimeType.isNotEmpty()
            ) {
                GlideApp.with(context).load(uri)
                    .into(binding.ivCaptureImage)
                Timber.d("raka %s", uri.toString())
                binding.ivCaptureImage.isEnabled = false
            }
            dialogUtils.dismiss()
        }
    }

    private fun setupDialog() {
        val li = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        dialogUtils =
            DialogUtils(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        dialogUtils.setInflater(R.layout.layout_attachment_preview, null, li).create()
    }

    @Suppress("DEPRECATION")
    private fun setupFabButton(fab: FloatingActionButton) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fab.colorFilter =
                BlendModeColorFilter(Color.parseColor("#FFFFFFFF"), BlendMode.SRC_ATOP)
        } else {
            fab.setColorFilter(Color.parseColor("#FFFFFFFF"), PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun pickLocation() {
        try {
            // Request location updates
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                locationListener
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        @SuppressLint("SetTextI18n")
        override fun onLocationChanged(location: Location) {
            latitudey = location.latitude
            longitudex = location.longitude
            binding.tvLocation.text = "$latitudey , $longitudex"
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Timber.tag(TAG).d("locationListener() onStatusChanged")
        }

        override fun onProviderEnabled(provider: String) {
            Timber.tag(TAG).d("locationListener() onProviderEnabled")
        }

        override fun onProviderDisabled(provider: String) {
            Timber.tag(TAG).d("locationListener() onProviderDisabled")
        }
    }

}