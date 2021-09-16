package id.thork.app.pages.labor_actual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityLaborActualBinding
import id.thork.app.databinding.ActivityLaborPlanBinding
import id.thork.app.pages.labor_actual.create_labor_actual.CreateLaborActualActivity
import id.thork.app.pages.labor_actual.element.LaborActualAdapter
import id.thork.app.pages.labor_actual.element.LaborActualViewModel
import id.thork.app.pages.labor_plan.SelectCraftActivity
import id.thork.app.pages.labor_plan.SelectLaborActivity
import id.thork.app.pages.labor_plan.SelectTaskActivity
import id.thork.app.pages.labor_plan.create_labor_plan.CreateLaborPlanActivity
import id.thork.app.pages.labor_plan.element.LaborPlanAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.pages.work_log.WorkLogActivity
import id.thork.app.persistence.entity.LaborActualEntity
import id.thork.app.persistence.entity.LaborPlanEntity
import timber.log.Timber

class LaborActualActivity  : BaseActivity() {
    val TAG = WorkLogActivity::class.java.name
    private val viewModels: LaborActualViewModel by viewModels()
    private val binding: ActivityLaborActualBinding by binding(R.layout.activity_labor_actual)
    private lateinit var laborActualAdapter: LaborActualAdapter
    private  lateinit var laborActualEntity: MutableList<LaborActualEntity>
    var intentWonum: String? = null
    var intentWorkorderid: Int? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@LaborActualActivity
            vm =viewModels

        }

        retriveFromIntent()

        laborActualEntity = mutableListOf()

        laborActualAdapter = LaborActualAdapter(this,laborActualEntity)

        binding.rvLaborActual.adapter = laborActualAdapter


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

        viewModels.getLaborActualList.observe(this, Observer {
            laborActualEntity.clear()
            laborActualEntity.addAll(it)
        })
    }

    private fun retriveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWorkorderid = intent.getIntExtra(BaseParam.WORKORDERID,0)
        Timber.d("wonum: %s && woid: %s", intentWonum.toString(), intentWorkorderid.toString())

        intentWorkorderid.whatIfNotNull {
            viewModels.fetchListLaborActual(it.toString())
        }

        intentWonum.whatIfNotNull {
//            viewModels.fetchWoCache(it)
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnCreate.setOnClickListener {
            val intent = Intent(this@LaborActualActivity, CreateLaborActualActivity::class.java)
            intent.putExtra(BaseParam.WORKORDERID, intentWorkorderid.toString())
            intent.putExtra(BaseParam.WONUM, intentWonum.toString())
            startActivity(intent)
            finish()
        }
    }

}
