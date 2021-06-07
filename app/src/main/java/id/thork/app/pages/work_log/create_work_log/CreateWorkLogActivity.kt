package id.thork.app.pages.work_log.create_work_log

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityCreateWorkLogBinding
import id.thork.app.databinding.ActivityWorkLogBinding
import id.thork.app.pages.find_asset_location.FindAssetActivity
import id.thork.app.pages.work_log.element.WorkLogAdapter
import id.thork.app.pages.work_log.element.WorkLogViewModel
import id.thork.app.pages.work_log.type.WorkLogTypeActivity
import id.thork.app.persistence.entity.WorklogEntity

class CreateWorkLogActivity  : BaseActivity() {
    val TAG = FindAssetActivity::class.java.name
    private val viewModels: WorkLogViewModel by viewModels()
    private val binding: ActivityCreateWorkLogBinding by binding(R.layout.activity_create_work_log)




    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@CreateWorkLogActivity
            vm =viewModels


        }


        setupToolbarWithHomeNavigation(
            getString(R.string.create_work_log),
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