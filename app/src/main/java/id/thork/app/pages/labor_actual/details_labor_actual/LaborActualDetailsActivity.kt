package id.thork.app.pages.labor_actual.details_labor_actual

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityCreateLaborActualBinding
import id.thork.app.databinding.ActivityLaborActualDetailsBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.labor_actual.create_labor_actual.CreateLaborActualActivity
import id.thork.app.pages.labor_actual.element.LaborActualViewModel
import id.thork.app.pages.labor_actual.element.TimePickerHelper
import id.thork.app.pages.labor_plan.SelectCraftActivity
import id.thork.app.pages.labor_plan.SelectLaborActivity
import id.thork.app.pages.labor_plan.SelectTaskActivity
import id.thork.app.persistence.entity.LaborActualEntity
import id.thork.app.utils.DateUtils
import id.thork.app.utils.InputFilterMinMaxUtils
import id.thork.app.utils.StringUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class LaborActualDetailsActivity : BaseActivity(), CustomDialogUtils.DialogActionListener {
    val TAG = CreateLaborActualActivity::class.java.name
    private val viewModels: LaborActualViewModel by viewModels()
    private val binding: ActivityCreateLaborActualBinding by binding(R.layout.activity_create_labor_actual)
    private var isCraft: Boolean = false
    private var intentWonum: String? = null
    private var intentWorkorderid: String? = null
    private var intentLaborcode: String? = null
    private var intentCraft: String? = null
    private var msStartDate : Long? = null
    private var msEndDate : Long? = null
    private var msStartTime : Long? = null
    private var msEndTime : Long? = null
    private var cal: Calendar = Calendar.getInstance()
    private var cal2 : Calendar = Calendar.getInstance()
    private var calForStartDate: String? = null
    private var calForEndDate : String? = null
    private var calForStarTime: String? = null
    private var calForEndTime : String? = null
    private var startDateObjectBoxFormat : String? = null
    private var endDateObjectBoxFormat : String? = null


    lateinit var timePicker: TimePickerHelper

    private lateinit var dialogUtils: DialogUtils
    private lateinit var customDialogUtils: CustomDialogUtils

    private var laborcode : String? = null
    private var craft : String? = null
    private var skillLevel : String? = null
    private var vendor : String? = null

    private var laborActualActivity : LaborActualEntity? = null


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@LaborActualDetailsActivity
            vm = viewModels
        }
        timePicker = TimePickerHelper(this, is24HourView = true, isSpinnerType = true)
        setupDatePicker()
        goToAnotherAct()


        dialogUtils = DialogUtils(this)
        customDialogUtils = CustomDialogUtils(this)

        setupToolbarWithHomeNavigation(
            getString(R.string.labor_actual),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )


        retriveFromIntent()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.getLaborActual.observe(this, androidx.lifecycle.Observer {
            laborActualActivity = it
            laborcode = StringUtils.NVL(it.laborcode, BaseParam.APP_DASH)
            craft = StringUtils.NVL(it.craft, BaseParam.APP_DASH)
            skillLevel = StringUtils.NVL(it.skillLevel, BaseParam.APP_DASH)
            vendor =StringUtils.NVL(it.vendor, BaseParam.APP_DASH)

            binding.apply {
                tvLabor.text = laborcode
                tvCraft.text = craft
                tvSkillLevel.text = skillLevel
                tvVendor.text = vendor

            }

        })
    }

    private fun retriveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWorkorderid = intent.getStringExtra(BaseParam.WORKORDERID)
        intentLaborcode = intent.getStringExtra(BaseParam.LABORCODE)
        intentCraft = intent.getStringExtra(BaseParam.CRAFT)
        val intentObjectboxid = intent.getLongExtra(BaseParam.OBJECTBOXID, 0)
        Timber.tag(TAG)
            .d("retriveFromIntent() laborcode: %s & craft: %s", intentLaborcode, intentCraft)
        viewModels.fetchLaborActualByObjectboxid(intentObjectboxid)
//        viewModels.fetchWoCache(intentWonum.toString())
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
        binding.tvStartDate.setOnClickListener {
            DatePickerDialog(
                this@LaborActualDetailsActivity,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.tvEndDate.setOnClickListener {
            DatePickerDialog(
                this@LaborActualDetailsActivity,
                dateSetListener2,
                cal2.get(Calendar.YEAR),
                cal2.get(Calendar.MONTH),
                cal2.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    fun updateDateInView(date: Boolean) {
        Timber.d("updateDateInView() cal1:%s", cal.time.time)
        Timber.d("updateDateInView() cal2:%s", cal2.time.time)
        when (date) {
            true -> {
                binding.tvStartDate.text =DateUtils.getAppDateFormat(cal.time)
                msStartDate = cal.timeInMillis
                calForStartDate = DateUtils.getAppDateFormatMaximo(cal.time)
            }
            false -> {
                binding.tvEndDate.text =DateUtils.getAppDateFormat(cal2.time)
                msEndDate = cal2.timeInMillis
                calForEndDate = DateUtils.getAppDateFormatMaximo(cal2.time)
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.tvStartTime.setOnClickListener {showTimePickerDialog(true) }
        binding.tvEndTime.setOnClickListener {showTimePickerDialog(false) }
        binding.btnSaveLaborActual.setOnClickListener {
            validationEmpty()
            validationDateAndTime()
        }
    }

    private fun showTimePickerDialog(isStartDate : Boolean) {
        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)

        timePicker.showDialog(h, m, object : TimePickerHelper.Callback {
            @SuppressLint("SetTextI18n")
            override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                cal.set(Calendar.HOUR_OF_DAY,hourOfDay)
                cal.set(Calendar.MINUTE,minute)
                when (isStartDate){
                    true  ->{
                        binding.tvStartTime.text = DateUtils.getAppTimeFormat(cal.time)
                        msStartTime = cal.timeInMillis
                        calForStarTime = DateUtils.getAppTimeFormatMaximo(cal.time)
                    }

                    false -> {
                        binding.tvEndTime.text = DateUtils.getAppTimeFormat(cal.time)
                        msEndTime = cal.timeInMillis
                        calForEndTime = DateUtils.getAppTimeFormatMaximo(cal.time)
                    }
                }

                Timber.d("showTimePickerDialog longstart :%s end :%s", msStartTime, msEndTime)
            }
        })
    }


    private fun goToAnotherAct() {
        binding.apply {
            selectCraft.setOnClickListener {
                isCraft = true
                goToSelectLabor()
            }
            selectLabor.setOnClickListener {
                goToSelectLabor()
            }
            selectTask.setOnClickListener {
                goToSelectTask()
            }
        }
    }

    private fun validationDateAndTime(){
        if (validationEmpty()) {
            if (msEndTime!! < msStartTime!!) {
                Toast.makeText(this@LaborActualDetailsActivity, "Finish time has to be after Start time ", Toast.LENGTH_SHORT).show()
            }
            else if (msEndDate!! < msStartDate!!){
                Toast.makeText(this@LaborActualDetailsActivity, "Finish time has to be after Start time ", Toast.LENGTH_SHORT).show()
            }
            else{
                convertDateFormat()
                dialogSaveLaborActual()
            }

        }
    }
    private fun validationEmpty():Boolean{
        binding.apply {
            if (tvLabor.text.isNullOrBlank() ||
                tvStartTime.text.isNullOrBlank() ||
                tvEndTime.text.isNullOrBlank() ||
                tvStartDate.text.isNullOrBlank() ||
                tvEndDate.text.isNullOrBlank() ||
                tvCraft.text.isNullOrBlank())
            {

                Toast.makeText(this@LaborActualDetailsActivity, "field cannot be empty", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return  true
    }

    private fun goToSelectLabor() {
        Timber.d("goToSelectLabor :%s", isCraft)
        if (isCraft){
            val intent = Intent(this, SelectLaborActivity::class.java)
            intent.putExtra(BaseParam.LABORCODE_FORM, BaseParam.LABORCODE_FORM_ACTUAL)
            intent.putExtra(BaseParam.CRAFT_FORM_ACTUAL, BaseParam.CRAFT_FORM_ACTUAL)
            startActivityForResult(intent, BaseParam.REQUEST_CODE_LABOR)
        }else {
            val intent = Intent(this, SelectLaborActivity::class.java)
            intent.putExtra(BaseParam.LABORCODE_FORM, BaseParam.LABORCODE_FORM_ACTUAL)
            startActivityForResult(intent, BaseParam.REQUEST_CODE_LABOR)
        }
    }

    private fun goToSelectTask() {
        val intent = Intent(this, SelectTaskActivity::class.java)
        intent.putExtra(BaseParam.WONUM, intentWonum.toString())
        intent.putExtra(BaseParam.WORKORDERID, intentWorkorderid.toString())
        startActivityForResult(intent, BaseParam.REQUEST_CODE_TASK)
    }

    private fun goToSelectCraft() {
        val intent = Intent(this, SelectCraftActivity::class.java)
        intent.putExtra(BaseParam.LABORCODE, binding.tvLabor.text.toString())
        startActivityForResult(intent, BaseParam.REQUEST_CODE_CRAFT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
//                    BaseParam.REQUEST_CODE_TASK -> {
//                        //Handling when choose task
//                        data.whatIfNotNull {
//                            val taskidResult = it.getIntExtra(BaseParam.TASKID, 0)
//                            taskidResult.whatIfNotNull {
//                                taskid = it.toString()
//                            }
//
//                            val taskDescriptionResult = it.getStringExtra(BaseParam.DESCRIPTION)
//                            taskDescriptionResult.whatIfNotNull {
//                                taskdesc = it
//                            }
//                            binding.tvTask.text = taskid.plus(BaseParam.APP_DASH).plus(taskdesc)
//                        }
//                    }

                    BaseParam.REQUEST_CODE_LABOR -> {
                        // Handling when choose Labor dan fill craft
                        data.whatIfNotNull {
                            val laborcode = it.getStringExtra(BaseParam.LABORCODE_FORM)
                            val craft = it.getStringExtra(BaseParam.CRAFT_FORM)
                            val skillLevel = it.getStringExtra(BaseParam.SKILL_FORM)
                            binding.apply {
                                tvLabor.text = laborcode
                                tvCraft.text = craft
                                tvSkillLevel.text = skillLevel
//                                removeValidation()
                            }
                        }
                    }

//                    BaseParam.REQUEST_CODE_CRAFT -> {
//                        // Handling when choose craft
//                        data.whatIfNotNull {
//                            val craft = it.getStringExtra(BaseParam.CRAFT_FORM)
//                            binding.apply {
//                                tvCraft.text = craft
//                                tvLabor.text = BaseParam.APP_DASH
//                                removeValidation()
//                            }
//                        }
//                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDateFormat() {
        startDateObjectBoxFormat =
            calForStartDate + "T" + calForStarTime
        endDateObjectBoxFormat = calForEndDate + "T" + calForEndTime
        Timber.d("convertDateFormat Start %s ", startDateObjectBoxFormat)
        Timber.d("convertDateFormat End %s ", endDateObjectBoxFormat)
    }

    private fun dialogSaveLaborActual() {
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.labor_plan_title)
            .setDescription(R.string.labor_actual_question)
            .setListener(this)
        customDialogUtils.show()
    }


    override fun onRightButton() {
        binding.apply {
            viewModels.saveLaborActual(
                intentWonum.toString(),
                intentWorkorderid.toString(),
                tvLabor.text.toString(),
                tvCraft.text.toString(),
                startDateObjectBoxFormat.toString(),
                endDateObjectBoxFormat.toString(),
                tvSkillLevel.text.toString())
        }
        customDialogUtils.dismiss()
    }

    override fun onLeftButton() {
        customDialogUtils.dismiss()
    }

    override fun onMiddleButton() {
        TODO("Not yet implemented")
    }

}