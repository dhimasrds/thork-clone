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
import id.thork.app.pages.labor_plan.SelectCraftActivity
import id.thork.app.pages.labor_plan.SelectLaborActivity
import id.thork.app.pages.labor_plan.SelectTaskActivity
import id.thork.app.pages.labor_plan.create_labor_plan.CreateLaborPlanActivity
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.utils.StringUtils

class LaborPlanDetailsActivity : BaseActivity() {
    val TAG = CreateLaborPlanActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivityLaborPlanDetailsBinding by binding(R.layout.activity_labor_plan_details)
    private var intentLaborcode: String? = null
    private var intentWonum: String? = null
    private var intentWorkorderid: String? = null
    private var intentCraft: String? = null


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@LaborPlanDetailsActivity
            vm = viewModels
        }

        goToAnotherAct()

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
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.laborPlanCache.observe(this, Observer {
            val taskid = it.taskid
            val taskdesc = it.taskDescription
            val skill = StringUtils.NVL(it.skillLevel, BaseParam.APP_DASH)
            val vendor = StringUtils.NVL(it.vendor, BaseParam.APP_DASH)
            binding.apply {
                tvLabor.text = it.laborcode
                tvTask.text = taskid.plus(BaseParam.APP_DASH).plus(taskdesc)
                tvCraft.text = it.craft
                tvSkillLevel.text = skill
                tvVendor.text = vendor
            }
        })

        viewModels.woCache.observe(this, Observer {
            val status = it.status

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
        startActivity(intent)
    }

    private fun goToSelectLabor() {
        val intent = Intent(this, SelectLaborActivity::class.java)
        startActivity(intent)
    }

    private fun goToSelectCraft() {
        val intent = Intent(this, SelectCraftActivity::class.java)
        startActivity(intent)
    }

    private fun retriveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWorkorderid = intent.getStringExtra(BaseParam.WORKORDERID)
        intentLaborcode = intent.getStringExtra(BaseParam.LABORCODE)
        intentCraft = intent.getStringExtra(BaseParam.CRAFT)

        intentLaborcode.whatIfNotNull(
            whatIf = {
                viewModels.fetchLaborPlanByLaborCode(it, intentWorkorderid.toString())
            },
            whatIfNot = {
                viewModels.fetchLaborPlanByCraft(
                    intentCraft.toString(),
                    intentWorkorderid.toString()
                )
            }
        )

        viewModels.fetchWoCache(intentWonum.toString())

    }

    override fun setupListener() {
        super.setupListener()

    }

}