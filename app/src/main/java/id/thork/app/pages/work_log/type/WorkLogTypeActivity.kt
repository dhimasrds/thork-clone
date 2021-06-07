package id.thork.app.pages.work_log.type


import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityWorkLogTypeBinding
import id.thork.app.pages.find_asset_location.FindAssetActivity
import id.thork.app.pages.work_log.element.WorkLogViewModel
import id.thork.app.pages.work_log.element.WorklogTypeAdapter
import id.thork.app.persistence.entity.WorklogEntity

class WorkLogTypeActivity : BaseActivity() {
    val TAG = FindAssetActivity::class.java.name
    private val viewModels: WorkLogViewModel by viewModels()
    private val binding: ActivityWorkLogTypeBinding by binding(R.layout.activity_work_log_type)
    private lateinit var worklogTypeAdapter: WorklogTypeAdapter
    private lateinit var worklogEntity: MutableList<WorklogEntity>



    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@WorkLogTypeActivity
            vm =viewModels
        }
        worklogEntity = mutableListOf()
        worklogTypeAdapter = WorklogTypeAdapter(worklogEntity)

        binding.rvWorklogType.adapter = worklogTypeAdapter


        setupToolbarWithHomeNavigation(
            getString(R.string.type),
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