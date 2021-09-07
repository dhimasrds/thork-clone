package id.thork.app.pages.labor_plan.details_labor_plan

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityLaborPlanDetailsBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.labor_plan.LaborPlanActivity
import id.thork.app.pages.labor_plan.SelectCraftActivity
import id.thork.app.pages.labor_plan.SelectLaborActivity
import id.thork.app.pages.labor_plan.SelectTaskActivity
import id.thork.app.pages.labor_plan.create_labor_plan.CreateLaborPlanActivity
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.persistence.entity.LaborPlanEntity
import id.thork.app.utils.StringUtils
import timber.log.Timber

class LaborPlanDetailsActivity : BaseActivity(), CustomDialogUtils.DialogActionListener {
    val TAG = CreateLaborPlanActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivityLaborPlanDetailsBinding by binding(R.layout.activity_labor_plan_details)
    private var intentLaborcode: String? = null
    private var intentWonum: String? = null
    private var intentWorkorderid: String? = null
    private var intentCraft: String? = null
    private var laborPlanEntity: LaborPlanEntity? = null
    private var woCache: Boolean = false
    var taskid: String? = null
    var taskdesc: String? = null
    var taskrefwonum: String? = null
    private lateinit var customDialogUtils: CustomDialogUtils
    private var saveValidation: Boolean = false

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@LaborPlanDetailsActivity
            vm = viewModels
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.labor_plan_detail),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )

        retriveFromIntent()
        customDialogUtils = CustomDialogUtils(this)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.laborPlanCache.observe(this, Observer {
            laborPlanEntity = it
            taskid = it.taskid
            taskdesc = it.taskDescription
            taskrefwonum = it.wonumTask
            val isTask: Int? = it.isTask
            val vendor = StringUtils.NVL(it.vendor, BaseParam.APP_DASH)
            binding.apply {
                tvLabor.text = it.laborcode
                if (isTask != BaseParam.APP_FALSE) {
                    tvTask.text = taskid.plus(BaseParam.APP_DASH).plus(taskdesc)
                }
                tvCraft.text = it.craft
                tvVendor.text = vendor
            }
        })

        viewModels.woCache.observe(this, Observer {
            val status = it.status
            woCache = true
            if (status != BaseParam.WAPPR) {
                binding.apply {
                    selectCraft.visibility = View.INVISIBLE
                    selectLabor.visibility = View.INVISIBLE
                    selectTask.visibility = View.INVISIBLE
                    btnSaveLaborPlan.visibility = View.INVISIBLE
                }
            }
        })  
    }

    private fun goToAnotherAct() {
        binding.apply {
            selectCraft.setOnClickListener {
                goToSelectCraft()
            }
            selectLabor.setOnClickListener {
                goToSelectLabor()
            }
            selectTask.setOnClickListener {
                goToSelectTask()
            }
        }
    }

    private fun goToSelectTask() {
        val intent = Intent(this, SelectTaskActivity::class.java)
        intent.putExtra(BaseParam.WONUM, intentWonum.toString())
        intent.putExtra(BaseParam.WORKORDERID, intentWorkorderid.toString())
        startActivityForResult(intent, BaseParam.REQUEST_CODE_TASK)
    }

    private fun goToSelectLabor() {
        val intent = Intent(this, SelectLaborActivity::class.java)
        intent.putExtra(BaseParam.LABORCODE_FORM, BaseParam.APP_DETAIL)
        startActivityForResult(intent, BaseParam.REQUEST_CODE_LABOR)
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
                    BaseParam.REQUEST_CODE_TASK -> {
                        //Handling when choose task
                        data.whatIfNotNull {
                            val taskidResult = it.getIntExtra(BaseParam.TASKID, 0)
                            taskidResult.whatIfNotNull {
                                taskid = it.toString()
                            }

                            val taskDescriptionResult = it.getStringExtra(BaseParam.DESCRIPTION)
                            taskDescriptionResult.whatIfNotNull {
                                taskdesc = it
                            }

                            val taskRefwonum = it.getStringExtra(BaseParam.REFWONUM)
                            taskRefwonum.whatIfNotNull {
                                taskrefwonum = it
                            }
                            binding.tvTask.text = taskid.plus(BaseParam.APP_DASH).plus(taskdesc)
                        }
                    }

                    BaseParam.REQUEST_CODE_LABOR -> {
                        // Handling when choose Labor dan fill craft
                        data.whatIfNotNull {
                            val laborcode = it.getStringExtra(BaseParam.LABORCODE_FORM)
                            binding.tvLabor.text = laborcode
                            binding.tvCraft.text = BaseParam.APP_DASH
                        }
                    }

                    BaseParam.REQUEST_CODE_CRAFT -> {
                        // Handling when choose craft
                        data.whatIfNotNull {
                            val craft = it.getStringExtra(BaseParam.CRAFT_FORM)
                            val skill = it.getStringExtra(BaseParam.SKILL_FORM)
                            binding.tvCraft.text = craft
                            binding.tvSkillLevel.text = skill
                            binding.tvLabor.text = BaseParam.APP_DASH
                        }
                    }
                }
            }
        }
    }

    private fun retriveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWorkorderid = intent.getStringExtra(BaseParam.WORKORDERID)
        intentLaborcode = intent.getStringExtra(BaseParam.LABORCODE)
        intentCraft = intent.getStringExtra(BaseParam.CRAFT)
        val intentObjectboxid = intent.getLongExtra(BaseParam.OBJECTBOXID, 0)
        Timber.tag(TAG)
            .d("retriveFromIntent() laborcode: %s & craft: %s", intentLaborcode, intentCraft)
        viewModels.fetchLaborPlanByObjectboxid(intentObjectboxid)
        viewModels.fetchWoCache(intentWonum.toString())
    }

    override fun setupListener() {
        super.setupListener()
        goToAnotherAct()
        binding.btnSaveLaborPlan.setOnClickListener {
            saveValidation = true
            dialogSaveLaborplan()
        }

        binding.btnDelete.setOnClickListener {
            dialogDeleteLaborPlan()
        }
    }

    private fun dialogDeleteLaborPlan() {
        customDialogUtils.setTitle(R.string.information)
        customDialogUtils.setDescription(R.string.labor_plan_delete)
        customDialogUtils.setRightButtonText(R.string.dialog_yes)
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
        customDialogUtils.setListener(this)
        customDialogUtils.show()
    }

    private fun dialogSaveLaborplan() {
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.labor_plan_title)
            .setDescription(R.string.labor_plan_question)
            .setListener(this)
        customDialogUtils.show()
    }

    override fun goToPreviousActivity() {
        super.goToPreviousActivity()
        navigateToLaborPlan()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToLaborPlan()
    }

    private fun navigateToLaborPlan() {
        val intent = Intent(this, LaborPlanActivity::class.java)
        intent.putExtra(BaseParam.WONUM, intentWonum)
        intent.putExtra(BaseParam.WORKORDERID, intentWorkorderid?.toInt())
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveValidation = false
        customDialogUtils.dismiss()
    }

    override fun onPause() {
        super.onPause()
        saveValidation = false
        customDialogUtils.dismiss()
    }

    override fun onRightButton() {
        if (saveValidation) {
            savelaborplan()
        } else {
            laborPlanEntity.whatIfNotNull {
                viewModels.removeLaborPlanEntity(it, woCache)
                navigateToLaborPlan()
            }
        }

    }

    override fun onLeftButton() {
        saveValidation = false
        customDialogUtils.dismiss()
    }

    override fun onMiddleButton() {
        customDialogUtils.dismiss()
    }

    private fun savelaborplan() {
        val labor = binding.tvLabor.text
        val craft = binding.tvCraft.text
        Timber.d(
            "savelaborplan() save labor plan : %s %s %s",
            labor,
            craft,
            taskid.toString()
        )
        laborPlanEntity.whatIfNotNull {
            viewModels.updateToLocalCache(
                it,
                labor.toString(),
                taskid.toString(),
                taskdesc.toString(),
                taskrefwonum.toString(),
                craft.toString(),
            )
        }
        navigateToLaborPlan()
    }

}