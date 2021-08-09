package id.thork.app.pages.labor_plan

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityLaborPlanBinding
import id.thork.app.pages.labor_plan.create_labor_plan.CreateLaborPlanActivity
import id.thork.app.pages.labor_plan.element.LaborPlanAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.pages.work_log.WorkLogActivity
import id.thork.app.persistence.entity.LaborPlanEntity
import timber.log.Timber

class LaborPlanActivity : BaseActivity() {
    val TAG = WorkLogActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivityLaborPlanBinding by binding(R.layout.activity_labor_plan)
    private lateinit var laborPlanAdapter: LaborPlanAdapter
    private lateinit var laborPlanEntity: MutableList<LaborPlanEntity>
    var intentWonum: String? = null
    var intentWorkorderid: Int? = null


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@LaborPlanActivity
            vm = viewModels


        }
        laborPlanEntity = mutableListOf()
        laborPlanAdapter = LaborPlanAdapter(this, laborPlanEntity)

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
        retriveFromIntent()
        viewModels.fetchMasterCraft()
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModels.getLaborPlanList.observe(this, Observer {
            Timber.d("LaborPlanActivity size: %s", it.size)
            laborPlanEntity.clear()
            laborPlanEntity.addAll(it)
            laborPlanAdapter.notifyDataSetChanged()
        })

        viewModels.woCache.observe(this, Observer {
            val status = it.status
            if(status != BaseParam.WAPPR) {
                binding.apply {
                    btnCreate.visibility = View.INVISIBLE
                }
            }
        })
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnCreate.setOnClickListener {
            val intent = Intent(this@LaborPlanActivity, CreateLaborPlanActivity::class.java)
            intent.putExtra(BaseParam.WORKORDERID, intentWorkorderid.toString())
            intent.putExtra(BaseParam.WONUM, intentWonum.toString())
            startActivity(intent)
            finish()
        }
    }

    private fun retriveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWorkorderid = intent.getIntExtra(BaseParam.WORKORDERID,0)
        Timber.d("wonum: %s && woid: %s", intentWonum.toString(), intentWorkorderid.toString())

        intentWorkorderid.whatIfNotNull {
            viewModels.fetchListLaborPlan(it.toString())
        }

        intentWonum.whatIfNotNull {
            viewModels.fetchWoCache(it)
        }
    }
}
