package id.thork.app.pages.labor_plan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivitySelectLaborBinding
import id.thork.app.databinding.ActivitySelectSkillLevelBinding
import id.thork.app.pages.labor_plan.element.LaborPlanAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.pages.labor_plan.element.SkilLevelAdapter

class SelectSkillLevelActivity : BaseActivity() {
    val TAG = SelectSkillLevelActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivitySelectSkillLevelBinding by binding(R.layout.activity_select_skill_level)
    private lateinit var skilLevelAdapter: SkilLevelAdapter


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@SelectSkillLevelActivity
            vm =viewModels

        }
        skilLevelAdapter = SkilLevelAdapter()

        binding.rvSelectSkill.adapter = skilLevelAdapter


        setupToolbarWithHomeNavigation(
            getString(R.string.select_skill_level),
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
