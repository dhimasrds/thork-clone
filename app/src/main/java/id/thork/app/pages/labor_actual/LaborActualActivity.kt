package id.thork.app.pages.labor_actual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityLaborActualBinding
import id.thork.app.databinding.ActivityLaborPlanBinding
import id.thork.app.pages.labor_actual.create_labor_actual.CreateLaborActualActivity
import id.thork.app.pages.labor_actual.element.LaborActualViewModel
import id.thork.app.pages.labor_plan.create_labor_plan.CreateLaborPlanActivity
import id.thork.app.pages.labor_plan.element.LaborPlanAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.pages.work_log.WorkLogActivity

class LaborActualActivity  : BaseActivity() {
    val TAG = WorkLogActivity::class.java.name
    private val viewModels: LaborActualViewModel by viewModels()
    private val binding: ActivityLaborActualBinding by binding(R.layout.activity_labor_actual)
    private lateinit var laborPlanAdapter: LaborPlanAdapter


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@LaborActualActivity
            vm =viewModels

            btnCreate.setOnClickListener {
                val intent = Intent(this@LaborActualActivity, CreateLaborActualActivity::class.java)
                startActivity(intent)
            }
        }
//        laborPlanAdapter = LaborPlanAdapter(thi)

        binding.rvLaborActual.adapter = laborPlanAdapter


        setupToolbarWithHomeNavigation(
            getString(R.string.labor_actual),
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
