package id.thork.app.pages.profiles.attendance


import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityHistoryAttendanceBinding
import id.thork.app.pages.profiles.attendance.element.AttandanceViewModel
import id.thork.app.pages.profiles.attendance.element.HistoryAttendanceAdapter

class HistoryAttendanceActivity : BaseActivity() {
    val TAG = HistoryAttendanceActivity::class.java.name
    private val viewModels: AttandanceViewModel by viewModels()
    private val binding: ActivityHistoryAttendanceBinding by binding(R.layout.activity_history_attendance)
    private lateinit var historyAttendanceAdapter: HistoryAttendanceAdapter

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@HistoryAttendanceActivity
            vm = viewModels

        }
        historyAttendanceAdapter = HistoryAttendanceAdapter()

        binding.rvHistoryAttendance.adapter = historyAttendanceAdapter

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