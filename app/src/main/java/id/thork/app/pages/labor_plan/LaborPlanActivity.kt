package id.thork.app.pages.labor_plan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityLaborPlanBinding
import id.thork.app.databinding.ActivityWorkLogBinding
import id.thork.app.pages.labor_plan.create_labor_plan.CreateLaborPlanActivity
import id.thork.app.pages.labor_plan.element.LaborPlanAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.pages.work_log.WorkLogActivity
import id.thork.app.pages.work_log.create_work_log.CreateWorkLogActivity
import id.thork.app.pages.work_log.element.WorkLogAdapter
import id.thork.app.pages.work_log.element.WorkLogViewModel
import id.thork.app.persistence.entity.WorklogEntity

class LaborPlanActivity : BaseActivity() {
    val TAG = WorkLogActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivityLaborPlanBinding by binding(R.layout.activity_labor_plan)
    private lateinit var laborPlanAdapter: LaborPlanAdapter


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@LaborPlanActivity
            vm =viewModels

            btnCreate.setOnClickListener {
                val intent = Intent(this@LaborPlanActivity, CreateLaborPlanActivity::class.java)
                startActivity(intent)
            }
        }
        laborPlanAdapter = LaborPlanAdapter()

        binding.rvLaborPlan.adapter = laborPlanAdapter


        setupToolbarWithHomeNavigation(
            getString(R.string.labor_plan),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )
    }

    override fun setupObserver() {
        super.setupObserver()
    }
}
