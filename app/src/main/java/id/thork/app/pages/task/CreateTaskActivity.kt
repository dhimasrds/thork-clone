package id.thork.app.pages.task

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.text.InputFilter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityCreateTaskBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.task.element.TaskViewModel
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
    private var scheduleStartObjectBox: String? = null
    private var actualStartObjectBox: String? = null
    private var dateValidation: Boolean = true
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
        intentTaskId = intent.getIntExtra(BaseParam.TASKID, 0)
        intentStatus.whatIfNotNullOrEmpty {
            setupStatus(it)
        }
        binding.tvIdTask.text = intentTaskId.toString()
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
            desc = binding.tvDesc.text.toString()
            validationDate()
            if (!desc.isNullOrEmpty() && scheduleStartObjectBox != null && estDur != null && actualStartObjectBox != null && dateValidation) {
                setDialogSaveTask()
            } else if (!dateValidation) {
                setDialogErrorDateTask()
            } else {
                setDialogErrorTask()
            }
        }
    }

    fun validationDate() {
        val longStartDate = cal.timeInMillis
        val longEndDate = cal2.timeInMillis

        binding.apply {
            val etScheduleStart = tvScheduleStart.text.toString().trim()
            val etActualStart = tvActualStart.text.toString().trim()
            if (etScheduleStart.isNotBlank() || etActualStart.isNotBlank()) {
                dateValidation = longEndDate >= longStartDate
            }
        }
    }

    fun setupDatePicker() {
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

        binding.tvActualStart.setOnClickListener {
            DatePickerDialog(
                this@CreateTaskActivity,
                dateSetListener2,
                cal2.get(Calendar.YEAR),
                cal2.get(Calendar.MONTH),
                cal2.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    fun updateDateInView(date: Boolean) {
        val dateFormatObjectBox = "yyyy-MM-dd'T'HH:mm:ss" // mention the format you need
        val dateFormat = "MM/dd/yyyy" // mention the format you need
        val sdfObjectBox = SimpleDateFormat(dateFormatObjectBox)
        val sdf = SimpleDateFormat(dateFormat)
        Timber.d("Datepicker :%s", date)
        Timber.d("Datepicker :%s", cal.time.time)
        when (date) {
            true -> {
                scheduleStartObjectBox = sdfObjectBox.format(cal.time)
                binding.tvScheduleStart.setText(sdf.format(cal.time))
            }
            false -> {
                actualStartObjectBox = sdfObjectBox.format(cal2.time)
                binding.tvActualStart.setText(sdf.format(cal2.time))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    var setEstimdur = View.OnClickListener {
        dialogUtils.setInflater(R.layout.dialog_estdur, null, layoutInflater).create()
        dialogUtils.show()
        val esdurHours = dialogUtils.setViewId(R.id.esdur_hours) as EditText
        val esdurMinutes = dialogUtils.setViewId(R.id.esdur_minutes) as EditText
        esdurHours.filters = arrayOf<InputFilter>(InputFilterMinMaxUtils(0, 24))
        esdurMinutes.filters = arrayOf<InputFilter>(InputFilterMinMaxUtils(0, 60))
        val saveEst = dialogUtils.setViewId(R.id.save_est) as Button
        saveEst.setOnClickListener {
            if (esdurHours.text.toString().isEmpty() && esdurMinutes.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.estimatedhour_estimatedminute,
                    Toast.LENGTH_LONG
                ).show()
            } else if (esdurHours.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.estimatedhour,
                    Toast.LENGTH_LONG
                ).show()
            } else if (esdurMinutes.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.estimatedminute,
                    Toast.LENGTH_LONG
                ).show()
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

    private fun setDialogErrorTask() {
        customDialogUtils
            .setMiddleButtonText(R.string.dialog_yes)
            .setTittle(R.string.error_task_title)
            .setDescription(R.string.error_task_qustion)
            .setListener(this)
        customDialogUtils.show()
    }

    private fun setDialogErrorDateTask() {
        customDialogUtils
            .setMiddleButtonText(R.string.dialog_yes)
            .setTittle(R.string.error_date_task_title)
            .setDescription(R.string.error_date_task_qustion)
            .setListener(this)
        customDialogUtils.show()
    }

    override fun onRightButton() {
        if (cancelTaskValidation) {
            gotoTaskActivity()
        } else {
            viewModels.saveCache(
                intentWoid, intentWonum, intentTaskId,
                desc, scheduleStartObjectBox, estDur,
                actualStartObjectBox, intentStatus
            )
            gotoTaskActivity()
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
        if (!desc.isNullOrEmpty() || tvscheduleStart.isNotEmpty() || estDur != null || tvactualStart.isNotEmpty()) {
            setDialogCancelTask()
        } else {
            finish()
        }
    }

    override fun goToPreviousActivity() {
        onBackPressed()
    }
}