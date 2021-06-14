package id.thork.app.pages.profiles.attendance

import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityHistoryAttendanceDetailsBinding
import id.thork.app.pages.profiles.attendance.element.AttandanceViewModel

class HistoryAttendanceDetailsActivity : BaseActivity() {
    val TAG = HistoryAttendanceDetailsActivity::class.java.name
    private val viewModels: AttandanceViewModel by viewModels()
    private val binding: ActivityHistoryAttendanceDetailsBinding by binding(R.layout.activity_history_attendance_details)


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
            option = false
        )
    }


}