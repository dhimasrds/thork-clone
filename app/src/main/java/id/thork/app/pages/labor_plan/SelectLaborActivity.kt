package id.thork.app.pages.labor_plan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivitySelectCraftBinding
import id.thork.app.databinding.ActivitySelectLaborBinding
import id.thork.app.pages.labor_plan.element.CraftAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel

class SelectLaborActivity : BaseActivity() {
    val TAG = SelectLaborActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivitySelectLaborBinding by binding(R.layout.activity_select_labor)
    private lateinit var laborPlanAdapter: LaborPlanAdapter


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@SelectLaborActivity
            vm =viewModels

        }
        laborPlanAdapter = LaborPlanAdapter()

        binding.rvSelectLabor.adapter = laborPlanAdapter


        setupToolbarWithHomeNavigation(
            getString(R.string.select_labor),
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
