package id.thork.app.pages.labor_plan.create_labor_plan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityCreateLaborPlanBinding
import id.thork.app.databinding.ActivityLaborPlanBinding
import id.thork.app.pages.labor_plan.SelectCraftActivity
import id.thork.app.pages.labor_plan.SelectLaborActivity
import id.thork.app.pages.labor_plan.SelectSkillLevelActivity
import id.thork.app.pages.labor_plan.SelectTaskActivity
import id.thork.app.pages.labor_plan.element.LaborPlanAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.pages.list_material.ListMaterialActivity
import id.thork.app.pages.work_log.WorkLogActivity

class CreateLaborPlanActivity : BaseActivity() {
    val TAG = WorkLogActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivityCreateLaborPlanBinding by binding(R.layout.activity_create_labor_plan)


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
    }

    override fun setupObserver() {
        super.setupObserver()
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
            selectSkillLevel.setOnClickListener {
                goToSelectSkill()
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

    private fun goToSelectSkill() {
        val intent = Intent(this, SelectSkillLevelActivity::class.java)
        startActivity(intent)
    }
}