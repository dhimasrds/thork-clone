package id.thork.app.pages.settings_log_detail

import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivitySettingsLogDetailBinding
import id.thork.app.pages.settings_log_detail.element.LogDetailViewModel
import id.thork.app.persistence.entity.LogEntity
import id.thork.app.utils.DateUtils

/**
 * Created by Raka Putra on 1/19/21
 * Jakarta, Indonesia.
 */
class LogDetailActivity : BaseActivity() {
    private val binding: ActivitySettingsLogDetailBinding by binding(R.layout.activity_settings_log_detail)
    private val viewModel: LogDetailViewModel by viewModels()

    override fun setupView() {
        super.setupView()
        initView()
        retrieveFromIntent()
    }

    private fun initView() {
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

    private fun retrieveFromIntent() {
        val intent = intent
        val id = intent.getLongExtra(BaseParam.ID, 0)
        val logEntity: LogEntity = viewModel.findLogs(id)
        if (logEntity != null) {
            val message: String = DateUtils.getDateTimeOB(logEntity.createdDate)
                .toString() + " # " + logEntity.message
            binding.tvLogDetail.text = message
        }
    }
}