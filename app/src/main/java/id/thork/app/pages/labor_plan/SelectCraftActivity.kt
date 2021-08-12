package id.thork.app.pages.labor_plan

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivitySelectCraftBinding
import id.thork.app.pages.labor_plan.element.CraftAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.persistence.entity.CraftMasterEntity

class SelectCraftActivity : BaseActivity() {
    val TAG = SelectCraftActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivitySelectCraftBinding by binding(R.layout.activity_select_craft)
    private lateinit var craftAdapter: CraftAdapter
    private lateinit var craftEntities: MutableList<CraftMasterEntity>


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@SelectCraftActivity
            vm = viewModels

        }

        craftEntities = mutableListOf()
        craftAdapter = CraftAdapter(this, craftEntities)
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
        retriveFromIntent()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.getCraftMaster.observe(this, Observer {
            craftEntities.clear()
            craftEntities.addAll(it)
            craftAdapter.notifyDataSetChanged()
        })

    }


    private fun retriveFromIntent() {
        viewModels.fetchMasterCraft()
    }
}
