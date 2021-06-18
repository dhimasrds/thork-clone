package id.thork.app.pages.profiles.history_attendance

import androidx.activity.viewModels
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityHistoryAttendanceDetailsBinding
import id.thork.app.network.GlideApp
import id.thork.app.pages.profiles.attendance.element.AttandanceViewModel
import id.thork.app.utils.DateUtils
import timber.log.Timber

class HistoryAttendanceDetailsActivity : BaseActivity() {
    val TAG = HistoryAttendanceDetailsActivity::class.java.name
    private val viewModels: AttandanceViewModel by viewModels()
    private val binding: ActivityHistoryAttendanceDetailsBinding by binding(R.layout.activity_history_attendance_details)
    var intentAttendanceId: Int? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@HistoryAttendanceDetailsActivity
            vm = viewModels
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.history_attendance),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )

        retrieveFromIntent()
        setupDisplay()
    }

    private fun retrieveFromIntent() {
        intentAttendanceId = intent.getIntExtra(BaseParam.ATTENDANCEID, 0)
        Timber.d("retrieveFromIntent() intentAttendanceId: %s", intentAttendanceId)
    }

    private fun setupDisplay() {
        intentAttendanceId.whatIfNotNull {
            val attendanceEntity = viewModels.findAttendanceById(it)
            attendanceEntity?.dateCheckInLocal.whatIfNotNull { dateCheckInLocal ->
                attendanceEntity?.dateCheckOutLocal.whatIfNotNull { dateCheckOutLocal ->
                    val longCheckIn = attendanceEntity?.longCheckIn
                    val latCheckIn = attendanceEntity?.latCheckIn
                    val longCheckOut = attendanceEntity?.longCheckOut
                    val latCheckOut = attendanceEntity?.latCheckOut
                    binding.apply {
                        cardAttendance
                        cardAttendance.tvCheckInDate.text =
                            DateUtils.getDateTimeCardView(dateCheckInLocal)
                        cardAttendance.tvCheckInTime.text =
                            DateUtils.getCheckAttendance(dateCheckInLocal)
                        cardAttendance.tvCheckOutDate.text =
                            DateUtils.getDateTimeCardView(dateCheckOutLocal)
                        cardAttendance.tvCheckOutTime.text =
                            DateUtils.getCheckAttendance(dateCheckOutLocal)
                        cardAttendance.tvWorkHour.text = attendanceEntity?.workHours
                        cardAttendance.tvDateAttendance.text =
                            attendanceEntity?.dateTimeHeader
                        tvCheckInLocation.text = "$latCheckIn, $longCheckIn"
                        tvCheckOutLocation.text = "$latCheckOut, $longCheckOut"
                        GlideApp.with(BaseApplication.context).load(attendanceEntity?.uriImageCheckIn)
                            .into(ivCheckIn)
                        binding.ivCheckIn.isEnabled = false
                        GlideApp.with(BaseApplication.context).load(attendanceEntity?.uriImageCheckOut)
                            .into(ivCheckOut)
                        binding.ivCheckOut.isEnabled = false
                    }
                }
            }
        }
    }
}
