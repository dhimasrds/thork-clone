package id.thork.app.pages.profiles.history_attendance


import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityHistoryAttendanceBinding
import id.thork.app.pages.profiles.attendance.element.AttandanceViewModel
import id.thork.app.pages.profiles.history_attendance.element.HistoryAttendanceAdapter
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.persistence.entity.AttendanceEntity
import timber.log.Timber

class HistoryAttendanceActivity : BaseActivity() {
    val TAG = HistoryAttendanceActivity::class.java.name
    private val viewModels: AttandanceViewModel by viewModels()
    private val binding: ActivityHistoryAttendanceBinding by binding(R.layout.activity_history_attendance)
    private lateinit var historyAttendanceAdapter: HistoryAttendanceAdapter
    private lateinit var attendanceEntities: MutableList<AttendanceEntity>

    override fun setupView() {
        super.setupView()
        attendanceEntities = mutableListOf()
        historyAttendanceAdapter = HistoryAttendanceAdapter(this, this, attendanceEntities)

        binding.apply {
            lifecycleOwner = this@HistoryAttendanceActivity
            vm = viewModels

            rvHistoryAttendance.apply {
                layoutManager = LinearLayoutManager(this@HistoryAttendanceActivity)
                addItemDecoration(
                    DividerItemDecoration(
                        this@HistoryAttendanceActivity,
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = historyAttendanceAdapter
            }

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
        viewModels.fetchAttendance()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.attendance.observe(this, {
            attendanceEntities.clear()
            attendanceEntities.addAll(it)
            historyAttendanceAdapter.notifyDataSetChanged()
            Timber.tag(TAG).d("setupObserver() size: %s", attendanceEntities.size)
        })
    }

}