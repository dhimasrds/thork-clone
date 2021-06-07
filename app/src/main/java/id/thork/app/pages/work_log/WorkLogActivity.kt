package id.thork.app.pages.work_log


import android.content.Intent
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityWorkLogBinding
import id.thork.app.pages.find_asset_location.FindAssetActivity
import id.thork.app.pages.work_log.create_work_log.CreateWorkLogActivity
import id.thork.app.pages.work_log.element.WorkLogAdapter
import id.thork.app.pages.work_log.element.WorkLogViewModel
import id.thork.app.persistence.entity.WorklogEntity

class WorkLogActivity : BaseActivity() {
    val TAG = FindAssetActivity::class.java.name
    private val viewModels: WorkLogViewModel by viewModels()
    private val binding: ActivityWorkLogBinding by binding(R.layout.activity_work_log)
    private lateinit var workLogAdapter: WorkLogAdapter
    private lateinit var worklogEntity: MutableList<WorklogEntity>



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
            option = false
        )

    }

    override fun setupObserver() {
        super.setupObserver()

    }
}