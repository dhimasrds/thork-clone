package id.thork.app.pages.work_log.create_work_log

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityCreateWorkLogBinding
import id.thork.app.pages.work_log.WorkLogActivity
import id.thork.app.pages.work_log.element.WorkLogViewModel
import id.thork.app.pages.work_log.type.WorkLogTypeActivity
import id.thork.app.utils.StringUtils
import timber.log.Timber

class CreateWorkLogActivity : BaseActivity() {
    val TAG = CreateWorkLogActivity::class.java.name
    private val viewModels: WorkLogViewModel by viewModels()
    private val binding: ActivityCreateWorkLogBinding by binding(R.layout.activity_create_work_log)
    private var intentWonum: String? = null
    private var intentWoid: String? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@CreateWorkLogActivity
            vm = viewModels
        }
        setupToolbarWithHomeNavigation(
            getString(R.string.create_work_log),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )

        retrieveFromIntent()
    }

    private fun retrieveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWoid = intent.getStringExtra(BaseParam.WORKORDERID)
        binding.tvType.text = BaseParam.WORK
    }


    override fun setupObserver() {
        super.setupObserver()
        viewModels.result.observe(this, Observer {
            when (it) {
                BaseParam.APP_TRUE -> {
                    navigateToWorklog()
                }
            }
        })

    }

    override fun setupListener() {
        super.setupListener()
        binding.tvType.setOnClickListener {
            val intent = Intent(this, WorkLogTypeActivity::class.java)
            startActivityForResult(intent, 99)
        }

        binding.btnCreate.setOnClickListener {
            val summary = StringUtils.checkingString(binding.tvSummary.text.toString())
            val desc = StringUtils.checkingString(binding.tvDesc.text.toString())
            val type = binding.tvType.text.toString()
            viewModels.saveWorklog(
                summary,
                desc,
                type,
                intentWonum.toString(),
                intentWoid.toString()
            )
        }

    }


    private fun navigateToWorklog() {
        val intent = Intent(this, WorkLogActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, intentWoid)
        intent.putExtra(BaseParam.WONUM, intentWonum)
        startActivity(intent)
        finish()
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