package id.thork.app.pages.work_log.detail_work_log

import android.content.Intent
import android.text.Editable
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityCreateWorkLogBinding
import id.thork.app.pages.work_log.element.WorkLogViewModel
import timber.log.Timber

class DetailWorkLogActivity : BaseActivity() {
    val TAG = DetailWorkLogActivity::class.java.name
    private val viewModels: WorkLogViewModel by viewModels()
    private val binding: ActivityCreateWorkLogBinding by binding(R.layout.activity_create_work_log)

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@DetailWorkLogActivity
            vm = viewModels
        }
        binding.btnCreate.visibility = View.GONE
        setupToolbarWithHomeNavigation(
            getString(R.string.detail_work_log),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )

        retrieveFromIntent()
    }

    private fun retrieveFromIntent() {
        val intentWonum = intent.getStringExtra(BaseParam.WONUM)
        val summary = intent.getStringExtra(BaseParam.SUMMARY)

        viewModels.findWorklog(intentWonum.toString(), summary.toString())
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.worklogEntity.observe(this, Observer { worklog ->
            worklog.whatIfNotNull {
                binding.tvType.text = it.type
                binding.tvSummary.isEnabled = false
                binding.tvDesc.isEnabled = false
                binding.tvSummary.text = Editable.Factory.getInstance().newEditable(it.summary)
                binding.tvDesc.text = Editable.Factory.getInstance().newEditable(it.description)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            data.whatIfNotNull {
                val worklogtype = it.getStringExtra(BaseParam.WORKLOGTYPE)
                Timber.d("onActivityResult() worklog: %s", worklogtype)
                binding.tvType.text = worklogtype

            }
        }
    }
}