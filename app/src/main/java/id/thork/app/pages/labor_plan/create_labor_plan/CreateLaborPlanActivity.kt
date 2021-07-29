package id.thork.app.pages.labor_plan.create_labor_plan

import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityCreateLaborPlanBinding
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel

class CreateLaborPlanActivity : BaseActivity() {
    val TAG = CreateLaborPlanActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivityCreateLaborPlanBinding by binding(R.layout.activity_create_labor_plan)


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@CreateLaborPlanActivity
            vm = viewModels
        }

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