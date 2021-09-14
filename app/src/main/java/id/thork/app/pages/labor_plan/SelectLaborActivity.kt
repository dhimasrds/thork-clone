package id.thork.app.pages.labor_plan

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivitySelectLaborBinding
import id.thork.app.pages.labor_actual.element.SelectLaborAndCraftAdapter
import id.thork.app.pages.labor_plan.element.LaborAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.persistence.entity.CraftMasterEntity
import id.thork.app.persistence.entity.LaborMasterEntity
import timber.log.Timber

class SelectLaborActivity : BaseActivity() {
    val TAG = SelectLaborActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivitySelectLaborBinding by binding(R.layout.activity_select_labor)
    private var intentTag : String? = null
    private var isCraft : String? = null
    private lateinit var laborAdapter: LaborAdapter
    private lateinit var selectLaborAndCraftAdapter: SelectLaborAndCraftAdapter
    private lateinit var laborEntities: MutableList<LaborMasterEntity>
    private lateinit var craftMasterEntity: MutableList<CraftMasterEntity>

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@SelectLaborActivity
            vm = viewModels

        }
        isCraft = intent.getStringExtra(BaseParam.CRAFT_FORM_ACTUAL)

        laborEntities = mutableListOf()
        craftMasterEntity = mutableListOf()
        laborAdapter = LaborAdapter(this, laborEntities, retriveFromIntent())
        selectLaborAndCraftAdapter = SelectLaborAndCraftAdapter(this, craftMasterEntity, retriveFromIntent())


        if (intentTag.equals(BaseParam.LABORCODE_FORM_ACTUAL)){
            binding.rvSelectLabor.adapter = selectLaborAndCraftAdapter
        }else{
        binding.rvSelectLabor.adapter = laborAdapter
        }

        if (isCraft.equals(BaseParam.CRAFT_FORM_ACTUAL)) {
            setupToolbarWithHomeNavigation(
                getString(R.string.select_craft),
                navigation = false,
                filter = false,
                scannerIcon = false,
                notification = false,
                option = false,
                historyAttendanceIcon = false
            )
        }else {
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


        viewModels.fetchMasterLabor()
        viewModels.fetchListMasterCraft()
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModels.getLaborMaster.observe(this, Observer {
            laborEntities.clear()
            laborEntities.addAll(it)
            laborAdapter.notifyDataSetChanged()
        })

        viewModels.getCraftMasterEntity.observe(this, Observer {
            craftMasterEntity.clear()
            craftMasterEntity.addAll(it)
            selectLaborAndCraftAdapter.notifyDataSetChanged()
        })
    }

    private fun retriveFromIntent(): String {
         intentTag = intent.getStringExtra(BaseParam.LABORCODE_FORM)
        Timber.d("retriveFromIntent() intent tag %s", intentTag)
        return intentTag.toString()
//        viewModels.fetchMasterCraftByLaborcode(intentLaborCode.toString())
    }
}
