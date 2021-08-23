package id.thork.app.pages.work_log.type


import androidx.activity.viewModels
import androidx.lifecycle.Observer
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityWorkLogTypeBinding
import id.thork.app.pages.work_log.element.WorkLogViewModel
import id.thork.app.pages.work_log.element.WorklogTypeAdapter
import id.thork.app.persistence.entity.WorklogTypeEntity

class WorkLogTypeActivity : BaseActivity() {
    val TAG = WorkLogTypeActivity::class.java.name
    private val viewModels: WorkLogViewModel by viewModels()
    private val binding: ActivityWorkLogTypeBinding by binding(R.layout.activity_work_log_type)
    private lateinit var worklogTypeAdapter: WorklogTypeAdapter
    private lateinit var worklogTypeEntity: MutableList<WorklogTypeEntity>


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@WorkLogTypeActivity
            vm = viewModels
        }
        worklogTypeEntity = mutableListOf()
        worklogTypeAdapter = WorklogTypeAdapter(worklogTypeEntity, this)

        binding.rvWorklogType.adapter = worklogTypeAdapter

        setupToolbarWithHomeNavigation(
            getString(R.string.type),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )
        viewModels.initWorklogType()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.listWorklogType.observe(this, Observer {
            worklogTypeEntity.clear()
            worklogTypeEntity.addAll(it)
            worklogTypeAdapter.notifyDataSetChanged()
        })

    }
}