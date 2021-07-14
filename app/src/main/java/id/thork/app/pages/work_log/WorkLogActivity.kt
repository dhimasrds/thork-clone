package id.thork.app.pages.work_log


import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityWorkLogBinding
import id.thork.app.pages.work_log.create_work_log.CreateWorkLogActivity
import id.thork.app.pages.work_log.element.WorkLogAdapter
import id.thork.app.pages.work_log.element.WorkLogViewModel
import id.thork.app.persistence.entity.WorklogEntity

class WorkLogActivity : BaseActivity() {
    val TAG = WorkLogActivity::class.java.name
    private val viewModels: WorkLogViewModel by viewModels()
    private val binding: ActivityWorkLogBinding by binding(R.layout.activity_work_log)
    private lateinit var workLogAdapter: WorkLogAdapter
    private lateinit var worklogEntity: MutableList<WorklogEntity>
    private var intentWonum: String? = null
    private var intentWoid: String? = null
    private var status: String? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@WorkLogActivity
            vm =viewModels

            btnCreate.setOnClickListener {
                val intent = Intent(this@WorkLogActivity, CreateWorkLogActivity::class.java)
                startActivity(intent)
            }
        }
        worklogEntity = mutableListOf()
        workLogAdapter = WorkLogAdapter(worklogEntity)

        binding.rvWorklog.adapter = workLogAdapter


        setupToolbarWithHomeNavigation(
            getString(R.string.work_log),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )
        retrieveFromIntent()
        validationView()
    }

    private fun retrieveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWoid = intent.getStringExtra(BaseParam.WORKORDERID)
        status = intent.getStringExtra(BaseParam.STATUS)
        intentWoid.whatIfNotNull {
            viewModels.initWorklog(it)
        }
    }

    fun validationView(){
        if (status.equals(BaseParam.CLOSED)){
            binding.btnCreate.visibility = View.GONE
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.listWorklog.observe(this, Observer {
            worklogEntity.clear()
            worklogEntity.addAll(it)
            workLogAdapter.notifyDataSetChanged()
        })
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnCreate.setOnClickListener {
            val intent = Intent(this, CreateWorkLogActivity::class.java)
            intent.putExtra(BaseParam.WORKORDERID, intentWoid)
            intent.putExtra(BaseParam.WONUM, intentWonum)
            startActivity(intent)
            finish()
        }
    }
}