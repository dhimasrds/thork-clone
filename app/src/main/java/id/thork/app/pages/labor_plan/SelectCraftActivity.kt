package id.thork.app.pages.labor_plan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityLaborPlanBinding
import id.thork.app.databinding.ActivitySelectCraftBinding
import id.thork.app.pages.labor_plan.create_labor_plan.CreateLaborPlanActivity
import id.thork.app.pages.labor_plan.element.CraftAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.pages.work_log.WorkLogActivity

class SelectCraftActivity: BaseActivity() {
    val TAG = SelectCraftActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivitySelectCraftBinding by binding(R.layout.activity_select_craft)
    private lateinit var craftAdapter: CraftAdapter


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@SelectCraftActivity
            vm =viewModels

        }
        craftAdapter = CraftAdapter()

        binding.rvSelectCraft.adapter = craftAdapter


        setupToolbarWithHomeNavigation(
            getString(R.string.select_craft),
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
