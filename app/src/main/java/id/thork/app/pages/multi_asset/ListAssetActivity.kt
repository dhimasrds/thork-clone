package id.thork.app.pages.multi_asset

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityListAssetBinding
import id.thork.app.pages.multi_asset.element.MultiAssetListAdapter
import id.thork.app.pages.multi_asset.element.MultiAssetViewModel

class ListAssetActivity : BaseActivity() {
    private val viewModels: MultiAssetViewModel by viewModels()
    private val binding: ActivityListAssetBinding by binding(R.layout.activity_list_asset)
    private lateinit var multiAssetAdapter: MultiAssetListAdapter

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@ListAssetActivity
            viewModel = viewModels
        }
        multiAssetAdapter = MultiAssetListAdapter()

       binding.recyclerView.adapter = multiAssetAdapter
        viewModels.getAllMultiAsset()

        setupToolbarWithHomeNavigation(
            getString(R.string.list_of_assets),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )

    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.getMultiAssetList.observe(this, Observer {
            multiAssetAdapter.setMultiAssetList(it)
        })
    }
}