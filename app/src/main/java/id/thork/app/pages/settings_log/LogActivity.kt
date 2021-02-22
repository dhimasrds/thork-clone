package id.thork.app.pages.settings_log

import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivitySettingsLogsBinding
import id.thork.app.pages.settings_log.element.LogAdapter
import id.thork.app.pages.settings_log.element.LogViewModel
import id.thork.app.pages.settings_log.element.RecyclerViewDividerItemDecoration
import id.thork.app.persistence.entity.LogEntity
import timber.log.Timber

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class LogActivity: BaseActivity() {
    val TAG = LogActivity::class.java.name
    private val binding: ActivitySettingsLogsBinding by binding(R.layout.activity_settings_logs)
    private val viewModel: LogViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LogAdapter
    private lateinit var toolBar: Toolbar
    private val logsEntities = mutableListOf<LogEntity>()

    override fun setupView() {
        super.setupView()
        setupToolbar()
        initAdapter()
        initLogsData()
    }

    private fun setupToolbar(){
        toolBar = binding.toolbarLog.wmsToolbar
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        binding.toolbarLog.toolbarTitle.text = getString(R.string.settings_logs_detail)
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initAdapter() {
        recyclerView = findViewById(R.id.recyclerview_settings_language)
        adapter = LogAdapter(this, logsEntities)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            RecyclerViewDividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL,
                16
            )
        )
        recyclerView.adapter = adapter
    }

    private fun initLogsData() {
        try {
            val logsEntities: List<LogEntity> = viewModel.findLogs()
            refreshItems(logsEntities)
        } catch (e: Exception) {
            Timber.e("fetchLogs() %s %s", e.message, e)
        }
    }

    private fun refreshItems(logsEntity: List<LogEntity>) {
        with(logsEntities) {
            clear()
            addAll(logsEntity)
        }
        adapter.notifyDataSetChanged()
    }
}