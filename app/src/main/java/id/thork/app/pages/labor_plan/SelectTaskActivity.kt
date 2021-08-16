package id.thork.app.pages.labor_plan

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivitySelectTaskBinding
import id.thork.app.pages.labor_plan.element.CraftAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanViewModel
import id.thork.app.pages.labor_plan.element.LaborTaskAdapter
import id.thork.app.persistence.entity.TaskEntity
import timber.log.Timber

class SelectTaskActivity : BaseActivity() {
    val TAG = SelectTaskActivity::class.java.name
    private val viewModels: LaborPlanViewModel by viewModels()
    private val binding: ActivitySelectTaskBinding by binding(R.layout.activity_select_task)
    private lateinit var laborTaskAdapter: LaborTaskAdapter
    private lateinit var taskEntities: MutableList<TaskEntity>
    private var intentWonum : String? = null
    private var intentWorkorderid : String? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@SelectTaskActivity
            vm = viewModels

        }
        taskEntities = mutableListOf()
        laborTaskAdapter = LaborTaskAdapter(this, taskEntities)

        binding.rvSelectTask.adapter = laborTaskAdapter


        setupToolbarWithHomeNavigation(
            getString(R.string.select_task),
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
        viewModels.taskList.observe(this, Observer {
            taskEntities.clear()
            taskEntities.addAll(it)
            laborTaskAdapter.notifyDataSetChanged()
        })
    }

    private fun retriveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWorkorderid = intent.getStringExtra(BaseParam.WORKORDERID)
        Timber.d("wonum: %s && woid: %s", intentWonum.toString(), intentWorkorderid.toString())

        intentWonum.whatIfNotNull {
            viewModels.fetchTask(it)
        }
    }


}