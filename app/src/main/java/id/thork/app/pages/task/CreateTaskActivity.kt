package id.thork.app.pages.task

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.text.InputFilter
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityCreateTaskBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.task.element.TaskViewModel
import id.thork.app.utils.CommonUtils
import id.thork.app.utils.DateUtils
import id.thork.app.utils.InputFilterMinMaxUtils
import id.thork.app.utils.StringUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class CreateTaskActivity : BaseActivity(), DialogUtils.DialogUtilsListener,
    CustomDialogUtils.DialogActionListener {
    val TAG = TaskActivity::class.java.name
    private val viewModels: TaskViewModel by viewModels()
    private val binding: ActivityCreateTaskBinding by binding(R.layout.activity_create_task)

    private lateinit var dialogUtils: DialogUtils
    private lateinit var customDialogUtils: CustomDialogUtils
    private var cal: Calendar = Calendar.getInstance()
    private var cal2: Calendar = Calendar.getInstance()

    private var estDur: Double? = null
    private var intentWoid: Int? = null
    private var intentTaskId: Int? = null
    private var desc: String? = null
    private var intentWonum: String? = null
    private var intentStatus: String? = null
    private var intentTag: String? = null
    private var intentDetailTag: String? = null
    private var intentDesc: String? = null
    private var intentSchdule: String? = null
    private var intentSchduleTime: String? = null
    private var intentActual: String? = null
    private var intentActualTime: String? = null
    private var intentEstDur: Double? = null
    private var dateSchedule: String? = null
    private var dateActual: String? = null
    private var timeSchedule: String? = null
    private var timeActual: String? = null
    private var scheduleDateFormatObjectBox: String? = null
    private var actualDateFormatObjectBox: String? = null
    private var cancelTaskValidation: Boolean = false

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@CreateTaskActivity
            vm = viewModels
        }
        setupToolbarWithHomeNavigation(
            getString(R.string.task_toolbar),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )
        dialogUtils = DialogUtils(this)
        customDialogUtils = CustomDialogUtils(this)


        retrieveFromIntent()
        setupDatePicker()
    }

    private fun retrieveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWoid = intent.getIntExtra(BaseParam.WORKORDERID, 0)
        intentStatus = intent.getStringExtra(BaseParam.STATUS)
        intentTag = intent.getStringExtra(BaseParam.TAG_TASK)
        intentTaskId = intent.getIntExtra(BaseParam.TASKID, 0)
        intentDesc = intent.getStringExtra(BaseParam.DESCRIPTION)
        intentSchdule = intent.getStringExtra(BaseParam.SHEDULE_START)
        intentSchduleTime = intent.getStringExtra(BaseParam.SHEDULE_START_TIME)
        intentActual = intent.getStringExtra(BaseParam.ACTUAL_START)
        intentEstDur = intent.getDoubleExtra(BaseParam.ESTDUR, 0.0)
        intentDetailTag = intent.getStringExtra(BaseParam.DETAIL_TASK)
        intentStatus.whatIfNotNullOrEmpty {
            setupStatus(it)
        }
        intentSchduleTime.whatIfNotNull {
            binding.tvTimeScheduleStart.setText(it)
        }
        intentSchdule.whatIfNotNull(
            whatIf = {
                binding.tvScheduleStart.setText(it)
            }, whatIfNot = {
                updateDateInView(true)
            })
        intentDetailTag.whatIfNotNull {
            setupDetailTask(intentDesc, intentSchdule, intentActual, intentEstDur, intentTag)
        }
        binding.tvIdTask.text = intentTaskId.toString()
    }

    private fun setupDetailTask(
        intentDesc: String?,
        intentSchdule: String?,
        intentActual: String?,
        intentEstDur: Double?,
        intenttag: String?
    ) {
        Timber.tag(TAG).d("setupDetailTask() schedule: %s", intentSchdule)
        binding.apply {
            intentDesc.whatIfNotNull { description ->
                intentSchdule.whatIfNotNull { schedule ->
                    intentActual.whatIfNotNull { actual ->
                        intentEstDur.whatIfNotNull { estimateDuration ->
                            val isCreateWo: Boolean = intenttag != null
                            setupDateInView(isCreateWo, schedule, actual)
                            dateSchedule = schedule
                            dateActual = actual
                            estDur = estimateDuration
                            tvDesc.setText(description)
                            tvEstDur.text = estimateDuration.toString()
                        }
                    }
                }
            }
        }
    }

    private fun setupDateInView(
        isCreateWO: Boolean,
        schedule: String,
        actual: String
    ) {
        binding.tvScheduleStart.setText(DateUtils.convertDateTaskFormat(isCreateWO, schedule))
        binding.tvActualStart.setText(DateUtils.convertDateTaskFormat(isCreateWO, actual))
        binding.tvTimeScheduleStart.setText(DateUtils.convertTimeTaskFormat(isCreateWO, schedule))
        binding.tvTimeActualStart.setText(DateUtils.convertTimeTaskFormat(isCreateWO, actual))
    }

    private fun setupStatus(status: String?) {
        binding.apply {
            when (status) {
                BaseParam.APPROVED -> {
                    tvStatusTask.text = BaseParam.APPROVED
                    tvStatusTask.setTextColor(
                        ContextCompat.getColor(
                            tvStatusTask.context,
                            R.color.blueTextStatus
                        )
                    )
                    tvStatusTask.setBackgroundResource(R.drawable.bg_status)

                }
                BaseParam.INPROGRESS -> {
                    tvStatusTask.text = BaseParam.INPROGRESS
                    tvStatusTask.setTextColor(
                        ContextCompat.getColor(
                            tvStatusTask.context,
                            R.color.colorYellow
                        )
                    )
                    tvStatusTask.background =
                        ContextCompat.getDrawable(tvStatusTask.context, R.drawable.bg_status_yellow)
                }
                BaseParam.COMPLETED -> {
                    tvStatusTask.text = BaseParam.COMPLETED
                    tvStatusTask.setTextColor(
                        ContextCompat.getColor(
                            tvStatusTask.context,
                            R.color.colorGreen
                        )
                    )
                    tvStatusTask.setBackgroundResource(R.drawable.bg_status_green)
                }
                BaseParam.WAPPR -> {
                    tvStatusTask.text = BaseParam.WAPPR
                    tvStatusTask.setTextColor(
                        ContextCompat.getColor(
                            tvStatusTask.context,
                            R.color.blueTextStatus
                        )
                    )
                    tvStatusTask.setBackgroundResource(R.drawable.bg_status)
                }
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.tvEstDur.setOnClickListener(setEstimdur)

        binding.btnSaveTask.setOnClickListener {
            if (formValidation()) {
                convertDateFormat()
                when {
                    intentTag != null && intentDetailTag != null && formValidation() && dateValidation() -> {
                        setDialogUpdateFromCreateWO()
                    }
                    intentDetailTag != null && formValidation() && dateValidation() -> {
                        setDialogUpdateFromCreateWO()
                    }
                    formValidation() && dateValidation() -> {
                        setDialogSaveTask()
                    }
                    else -> {
                        CommonUtils.standardToast(getString(R.string.general_required_fields))
                    }
                }
            }
        }
    }

    private fun convertDateFormat() {
        val dt3 = SimpleDateFormat("MM/dd/yyyy")
        val dt4 = SimpleDateFormat("HH:mm")
        val schedule = dt3.parse(binding.tvScheduleStart.text.toString())
        val scheduleTime = dt4.parse(binding.tvTimeScheduleStart.text.toString())
        val actual = dt3.parse(binding.tvActualStart.text.toString())
        val actualTime = dt4.parse(binding.tvTimeActualStart.text.toString())
        val formatDate = SimpleDateFormat("yyyy-MM-dd")
        val formatTime = SimpleDateFormat("HH:mm:ss")
        scheduleDateFormatObjectBox =
            formatDate.format(schedule) + "T" + formatTime.format(scheduleTime)
        actualDateFormatObjectBox = formatDate.format(actual) + "T" + formatTime.format(actualTime)
        Timber.d("convertDateFormat schedule %s ", scheduleDateFormatObjectBox)
        Timber.d("convertDateFormat actual %s ", actualDateFormatObjectBox)
    }

    private fun formValidation(): Boolean {
        binding.apply {
            if (tvDesc.text.toString().isBlank()) {
                tvDesc.error = getString(R.string.task_desc_required)
                return false
            } else {
                desc = tvDesc.text.toString()
            }
            if (tvEstDur.text.toString().isBlank()) {
                tvEstDur.error = getString(R.string.task_estdur_required)
                return false
            }
            if (tvActualStart.text.toString().isBlank()) {
                tvActualStart.error = getString(R.string.task_actual_required)
                return false
            }
            if (tvTimeActualStart.text.toString().isBlank()) {
                tvTimeActualStart.error = getString(R.string.task_actual_required)
                return false
            }
        }
        return true
    }

    private fun dateValidation(): Boolean {
        val actualDate = DateUtils.convertMaximoDateToMillisec(actualDateFormatObjectBox)
        val a = DateUtils.getDateTimeMaximo()
        val todayDate = DateUtils.convertMaximoDateToMillisec(a)

        binding.apply {
            val etScheduleStart = tvScheduleStart.text.toString().trim()
            val etActualStart = tvActualStart.text.toString().trim()
            if (etScheduleStart.isNotBlank() || etActualStart.isNotBlank()) {
                return todayDate >= actualDate
            } else {
                return false
            }
        }
    }

    private fun setupDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView(true)
            }

        val dateSetListener2 =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal2.set(Calendar.YEAR, year)
                cal2.set(Calendar.MONTH, monthOfYear)
                cal2.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView(false)
            }

        val scheduledTimeListener =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                updateTimeInView(true)
            }

        val actualTimeListener =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal2.set(Calendar.HOUR_OF_DAY, hour)
                cal2.set(Calendar.MINUTE, minute)
                updateTimeInView(false)
            }

        binding.tvScheduleStart.setOnClickListener {
            DatePickerDialog(
                this@CreateTaskActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        binding.tvTimeScheduleStart.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this@CreateTaskActivity, R.style.ThorTimePickerDialog,
                scheduledTimeListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
            )
            timePickerDialog.show()
        }

        binding.tvActualStart.setOnClickListener {
            DatePickerDialog(
                this@CreateTaskActivity,
                dateSetListener2,
                cal2.get(Calendar.YEAR),
                cal2.get(Calendar.MONTH),
                cal2.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.tvTimeActualStart.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this@CreateTaskActivity, R.style.ThorTimePickerDialog,
                actualTimeListener, cal2.get(Calendar.HOUR_OF_DAY), cal2.get(Calendar.MINUTE), true
            )
            timePickerDialog.show()
        }
    }

    private fun updateDateInView(isSchedule: Boolean) {
        val dateFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(dateFormat)
        when (isSchedule) {
            true -> {
                dateSchedule = sdf.format(cal.time)
                binding.tvScheduleStart.setText(sdf.format(cal.time))
            }
            false -> {
                dateActual = sdf.format(cal2.time)
                binding.tvActualStart.setText(sdf.format(cal2.time))
            }
        }
    }

    fun updateTimeInView(isSchedule: Boolean) {
        when (isSchedule) {
            true -> {
                binding.tvTimeScheduleStart.setText(DateUtils.getAppTimeFormat(cal.time))
                timeSchedule = binding.tvTimeScheduleStart.text?.toString()
            }
            false -> {
                binding.tvTimeActualStart.setText(DateUtils.getAppTimeFormat(cal2.time))
                timeActual = binding.tvTimeActualStart.text?.toString()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    var setEstimdur = View.OnClickListener {
        dialogUtils.setInflater(R.layout.dialog_estdur, null, layoutInflater).create()
        dialogUtils.setCancelable(true)
        dialogUtils.show()
        val esdurHours = dialogUtils.setViewId(R.id.esdur_hours) as EditText
        val esdurMinutes = dialogUtils.setViewId(R.id.esdur_minutes) as EditText
        esdurHours.filters = arrayOf<InputFilter>(InputFilterMinMaxUtils(0, 24))
        esdurMinutes.filters = arrayOf<InputFilter>(InputFilterMinMaxUtils(0, 60))
        val saveEst = dialogUtils.setViewId(R.id.save_est) as Button
        saveEst.setOnClickListener {
            dialogUtils.setCancelable(false)
            if (esdurHours.text.toString().isEmpty() && esdurMinutes.text.toString().isEmpty()) {
                CommonUtils.standardToast(getString(R.string.estimatedhour_estimatedminute))
            } else if (esdurHours.text.toString().isEmpty()) {
                CommonUtils.standardToast(getString(R.string.estimatedhour))
            } else if (esdurMinutes.text.toString().isEmpty()) {
                CommonUtils.standardToast(getString(R.string.estimatedminute))
            } else {
                val estH: Int = esdurHours.text.toString().toInt()
                val estM: Int = esdurMinutes.text.toString().toInt()
                var estHour = estH.toString()
                var estMinute = estM.toString()
                estHour = StringUtils.convertTimeString(estHour)
                estMinute = StringUtils.convertTimeString(estMinute)
                binding.tvEstDur.text = "$estHour " + StringUtils.getStringResources(
                    this,
                    R.string.estHour
                ) + " : " + estMinute + " " + StringUtils.getStringResources(
                    this,
                    R.string.estMinute
                )
                binding.tvEstDur.error = null
                estDur = viewModels.estDuration(estH, estM)
                dialogUtils.dismiss()
            }
        }
    }

    override fun onPositiveButton() {
        Timber.d("onPositiveButton")
    }

    override fun onNegativeButton() {
        Timber.d("onNegativeButton")
    }

    private fun gotoTaskActivity() {
        val intent = Intent(this, TaskActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, intentWoid)
        intent.putExtra(BaseParam.WONUM, intentWonum)
        intent.putExtra(BaseParam.STATUS, intentStatus)
        intentTag.whatIfNotNull {
            intent.putExtra(BaseParam.TAG_TASK, intentTag)
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun setDialogSaveTask() {
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.task_title)
            .setDescription(R.string.task_qustion)
            .setListener(this)
        customDialogUtils.show()
    }

    private fun setDialogCancelTask() {
        cancelTaskValidation = true
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.task_cancel_title)
            .setDescription(R.string.task_cancel_qustion)
            .setListener(this)
        customDialogUtils.show()
    }

    private fun setDialogUpdateFromCreateWO() {
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.task_title)
            .setDescription(R.string.task_qustion)
            .setListener(this)
        customDialogUtils.show()
    }

    override fun onRightButton() {
        when {
            intentDetailTag != null -> {
                validateEditTask()
            }
            intentTag != null && !cancelTaskValidation -> {
                saveTaskFromCreateWo()
            }
            cancelTaskValidation -> {
                gotoTaskActivity()
            }
            else -> {
                validateOnlineOrOffline()
            }
        }
        gotoTaskActivity()
    }

    private fun validateEditTask() {
        if (intentTag != null) {
            viewModels.editTaskOnlineFromCreateWo(
                intentWoid,
                intentTaskId,
                intentWonum,
                desc,
                scheduleDateFormatObjectBox,
                estDur,
                actualDateFormatObjectBox
            )
        } else {
            viewModels.editTaskOnlineFromWoDetail(
                intentWoid,
                intentTaskId,
                intentWonum,
                desc,
                scheduleDateFormatObjectBox,
                estDur,
                actualDateFormatObjectBox
            )
        }
        CommonUtils.standardToast(getString(R.string.task_updated))
    }

    private fun updateTaskOfflineFromWoDetail() {
        viewModels.saveCache(
            intentWoid,
            intentWonum,
            intentTaskId,
            desc,
            scheduleDateFormatObjectBox,
            estDur,
            actualDateFormatObjectBox,
            intentStatus,
            BaseParam.APP_FALSE,
            BaseParam.APP_TRUE,
            BaseParam.APP_TRUE
        )
    }

    private fun updateTaskOnlineFromWoDetail() {
        viewModels.saveCache(
            intentWoid,
            intentWonum,
            intentTaskId,
            desc,
            scheduleDateFormatObjectBox,
            estDur,
            actualDateFormatObjectBox,
            intentStatus,
            BaseParam.APP_TRUE,
            BaseParam.APP_FALSE,
            BaseParam.APP_TRUE
        )
    }

    private fun saveTaskFromCreateWo() {
        viewModels.saveCacheFromCreateWo(
            intentWoid,
            intentWonum,
            intentTaskId,
            desc,
            scheduleDateFormatObjectBox,
            estDur,
            actualDateFormatObjectBox,
            intentStatus,
            BaseParam.APP_FALSE,
            BaseParam.APP_FALSE,
            BaseParam.APP_FALSE
        )
    }

    private fun validateOnlineOrOffline() {
        if (isConnected) {
            updateTaskOnlineFromWoDetail()
        } else {
            updateTaskOfflineFromWoDetail()
        }
    }

    override fun onLeftButton() {
        cancelTaskValidation = false
        customDialogUtils.dismiss()
    }

    override fun onMiddleButton() {
        customDialogUtils.dismiss()
    }

    override fun onResume() {
        super.onResume()
        customDialogUtils.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        customDialogUtils.dismiss()
    }

    override fun onBackPressed() {
        desc = binding.tvDesc.text.toString()
        val tvscheduleStart: String = binding.tvScheduleStart.text.toString()
        val tvactualStart: String = binding.tvActualStart.text.toString()
        if (intentDetailTag != null) {
            finish()
        } else if (!desc.isNullOrEmpty() || tvscheduleStart.isNotEmpty() || estDur != null || tvactualStart.isNotEmpty()) {
            setDialogCancelTask()
        } else {
            finish()
        }
    }

    override fun goToPreviousActivity() {
        onBackPressed()
    }
}