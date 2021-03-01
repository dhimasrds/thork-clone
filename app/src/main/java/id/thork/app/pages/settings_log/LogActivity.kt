package id.thork.app.pages.settings_log

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivitySettingsLogsBinding
import id.thork.app.pages.settings_log.element.LogAdapter
import id.thork.app.pages.settings_log.element.LogRecyclerViewItemClickListener
import id.thork.app.pages.settings_log.element.LogViewModel
import id.thork.app.pages.settings_log_detail.LogDetailActivity
import id.thork.app.persistence.entity.LogEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class LogActivity : BaseActivity() {
    val TAG = LogActivity::class.java.name
    private val binding: ActivitySettingsLogsBinding by binding(R.layout.activity_settings_logs)
    private val viewModel: LogViewModel by viewModels()

    private lateinit var adapter: LogAdapter

    companion object {
        val INTENT_EXTRA_KEY = "LOG_ID"
    }

    override fun setupView() {
        super.setupView()
        super.setupView()
        binding.apply {
            lifecycleOwner = this@LogActivity
            logsActivity = viewModel
        }
        setupToolbarWithHomeNavigation(getString(R.string.action_settings), navigation = false)
//        setupToolbar()
    }

    override fun setupObserver() {
        super.setupObserver()
        adapter = LogAdapter(object : LogRecyclerViewItemClickListener {
            override fun onItemClicked(logEntity: LogEntity) {
                startBorrowItemActivity(logEntity.id)
            }
        })
        binding.recyclerviewSettingsLogs.adapter = adapter
        binding.recyclerviewSettingsLogs.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            viewModel.logList.collect {
                adapter.submitData(it)

            }
        }
    }

    private fun startBorrowItemActivity(id: Long? = null) {
        val intent = Intent(this, LogDetailActivity::class.java)
        if (id != null) {
            intent.putExtra(INTENT_EXTRA_KEY, id)
        }
        startActivity(intent)
    }

}