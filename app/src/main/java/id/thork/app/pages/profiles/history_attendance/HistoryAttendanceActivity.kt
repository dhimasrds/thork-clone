package id.thork.app.pages.profiles.history_attendance


import android.app.DatePickerDialog
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityHistoryAttendanceBinding
import id.thork.app.pages.profiles.attendance.element.AttandanceViewModel
import id.thork.app.pages.profiles.history_attendance.element.HistoryAttendanceAdapter
import id.thork.app.persistence.entity.AttendanceEntity
import id.thork.app.utils.CommonUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class HistoryAttendanceActivity : BaseActivity() {
    val TAG = HistoryAttendanceActivity::class.java.name
    private val viewModels: AttandanceViewModel by viewModels()
    private val binding: ActivityHistoryAttendanceBinding by binding(R.layout.activity_history_attendance)
    private lateinit var historyAttendanceAdapter: HistoryAttendanceAdapter
    private lateinit var attendanceEntities: MutableList<AttendanceEntity>
    private var cal: Calendar = Calendar.getInstance()
    private var cal2: Calendar = Calendar.getInstance()
    private var clear = false


    override fun setupView() {
        super.setupView()
        attendanceEntities = mutableListOf()
        historyAttendanceAdapter = HistoryAttendanceAdapter(this, this, attendanceEntities)
        setupDatePicker()

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

        bindingAction()

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

    fun bindingAction() {
        binding.apply {
            clearText.setOnClickListener {
                startDate.text?.clear()
                endDate.text?.clear()
                setupObserver()
                clear = true
            }

            btnFilter.setOnClickListener {
                Timber.d("button filter :%s", cal.time)
                Timber.d("button filter :%s", cal2.time)
                validationDate()
            }
        }
    }

    fun validationDate() {
        val longStartDate = cal.timeInMillis
        val longEndDate = cal2.timeInMillis

        binding.apply {
            val etStartDate = startDate.text.toString().trim()
            val etEndDate = endDate.text.toString().trim()
            if (etStartDate.isNotBlank() || etEndDate.isNotBlank()) {
                if (longEndDate < longStartDate) {
                    CommonUtils.standardToast(getString(R.string.end_date_history))
                } else {
                    Timber.d("validationDate() filter :%s", startDate.text)
                    viewModels.filterByDate(cal.timeInMillis, cal2.timeInMillis)
                    setupViewFilter()
                }
            } else {
                CommonUtils.standardToast(getString(R.string.start_date_history))
            }
        }
    }

    fun setupDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal.set(Calendar.HOUR, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                updateDateInView(true)
            }

        val dateSetListener2 =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal2.set(Calendar.YEAR, year)
                cal2.set(Calendar.MONTH, monthOfYear)
                cal2.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal2.set(Calendar.HOUR, 23)
                cal2.set(Calendar.MINUTE, 59)
                cal2.set(Calendar.SECOND, 59)
                updateDateInView(false)
            }
        binding.startDate.setOnClickListener {
            DatePickerDialog(
                this@HistoryAttendanceActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.endDate.setOnClickListener {
            DatePickerDialog(
                this@HistoryAttendanceActivity,
                dateSetListener2,
                cal2.get(Calendar.YEAR),
                cal2.get(Calendar.MONTH),
                cal2.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    fun updateDateInView(date: Boolean) {
        val dateFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(dateFormat)
        Timber.d("updateDateInView :%s", date)
        Timber.d("updateDateInView() cal1:%s", cal.time.time)
        Timber.d("updateDateInView() cal2:%s", cal2.time.time)

        when (date) {
            true -> {
                binding.startDate.setText(sdf.format(cal.time))
            }
            false -> {
                binding.endDate.setText(sdf.format(cal2.time))
            }
        }
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

    fun setupViewFilter() {
        viewModels.filterBydate.observe(this, {
            attendanceEntities.clear()
            attendanceEntities.addAll(it)
            historyAttendanceAdapter.notifyDataSetChanged()
            Timber.tag(TAG).d("setupObserver() size: %s", attendanceEntities.size)
        })
    }
}