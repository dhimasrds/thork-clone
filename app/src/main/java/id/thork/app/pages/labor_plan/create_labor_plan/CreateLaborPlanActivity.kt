package id.thork.app.pages.labor_plan.create_labor_plan

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityCreateLaborPlanBinding
import id.thork.app.pages.labor_plan.LaborPlanActivity
import id.thork.app.pages.labor_plan.SelectCraftActivity
import id.thork.app.pages.labor_plan.SelectLaborActivity
import id.thork.app.pages.labor_plan.SelectTaskActivity
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import timber.log.Timber

class CreateLaborPlanActivity : BaseActivity() {
    val TAG = CreateLaborPlanActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivityCreateLaborPlanBinding by binding(R.layout.activity_create_labor_plan)
    var intentWonum: String? = null
    var intentWorkorderid: String? = null
    var taskid: String? = null
    var taskdesc: String? = null
    var isParentexisting : Boolean? = false

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@CreateLaborPlanActivity
            vm = viewModels
        }

        goToAnotherAct()

        setupToolbarWithHomeNavigation(
            getString(R.string.labor_plan),
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
        viewModels.craftEntity.observe(this, Observer {
            binding.apply {
                tvLabor.text = it.laborcode
                tvCraft.text = BaseParam.APP_DASH
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
        intent.putExtra(BaseParam.LABORCODE_FORM, BaseParam.APP_CREATE)
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
                            binding.tvLabor.text = BaseParam.APP_DASH
                        }
                    }
                }
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnSaveLaborActual.setOnClickListener {
            val laborcode = binding.tvLabor.text
            val craft = binding.tvCraft.text
            val skillLevel = binding.tvSkillLevel.text
            Timber.d("setupListener() Create labor plan: %s %s %s", laborcode, craft, skillLevel)
            viewModels.saveToLocalCache(
                laborcode.toString(),
                taskid.toString(),
                taskdesc.toString(),
                craft.toString(),
                intentWonum.toString(),
                intentWorkorderid.toString()
            )
            navigateToLaborPlan()

        }
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

    private fun retriveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWorkorderid = intent.getStringExtra(BaseParam.WORKORDERID)
    }


}