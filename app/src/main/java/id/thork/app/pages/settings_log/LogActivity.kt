package id.thork.app.pages.settings_log

import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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
    private val logsEntities = mutableListOf<LogEntity>()

    override fun setupView() {
        super.setupView()
        initToolbar()
        initAdapter()
        initLogsData()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.wms_toolbar)
        val mTitle = toolbar.findViewById<TextView>(R.id.toolbar_title)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        mTitle.setText(R.string.settings_logs_detail)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_toolbar))
        toolbar.setNavigationOnClickListener { finish() }
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