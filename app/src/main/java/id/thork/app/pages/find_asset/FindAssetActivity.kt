package id.thork.app.pages.find_asset

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityFindAssetBinding
import id.thork.app.databinding.ActivityListAssetBinding
import id.thork.app.pages.find_asset.element.FindAssetViewModel
import id.thork.app.pages.multi_asset.element.MultiAssetListAdapter
import id.thork.app.pages.multi_asset.element.MultiAssetViewModel

class FindAssetActivity : BaseActivity() {
    private val viewModels: FindAssetViewModel by viewModels()
    private val binding: ActivityFindAssetBinding by binding(R.layout.activity_find_asset)
    private lateinit var multiAssetAdapter: MultiAssetListAdapter

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@FindAssetActivity
            viewModel =viewModels
        }
        multiAssetAdapter = MultiAssetListAdapter()

        binding.recyclerView.adapter = multiAssetAdapter
        viewModels.findAllAsset()

        setupToolbarWithHomeNavigation(
            getString(R.string.find_asset),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )

    }
}