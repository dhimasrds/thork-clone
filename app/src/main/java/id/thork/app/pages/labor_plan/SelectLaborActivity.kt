package id.thork.app.pages.labor_plan

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivitySelectLaborBinding
import id.thork.app.pages.labor_plan.element.LaborAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.persistence.entity.LaborMasterEntity
import timber.log.Timber

class SelectLaborActivity : BaseActivity() {
    val TAG = SelectLaborActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivitySelectLaborBinding by binding(R.layout.activity_select_labor)
    private lateinit var laborAdapter: LaborAdapter
    private lateinit var laborEntities: MutableList<LaborMasterEntity>

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@SelectLaborActivity
            vm = viewModels

        }
        laborEntities = mutableListOf()
        laborAdapter = LaborAdapter(this, laborEntities, retriveFromIntent())

        binding.rvSelectLabor.adapter = laborAdapter


        setupToolbarWithHomeNavigation(
            getString(R.string.select_labor),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )

        viewModels.fetchMasterLabor()
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModels.getLaborMaster.observe(this, Observer {
            laborEntities.clear()
            laborEntities.addAll(it)
            laborAdapter.notifyDataSetChanged()
        })
    }

    private fun retriveFromIntent(): String {
        val intentTag = intent.getStringExtra(BaseParam.LABORCODE_FORM)
        Timber.d("retriveFromIntent() intent tag %s", intentTag)
        return intentTag.toString()
//        viewModels.fetchMasterCraftByLaborcode(intentLaborCode.toString())
    }
}
