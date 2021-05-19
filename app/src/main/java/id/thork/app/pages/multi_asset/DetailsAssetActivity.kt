package id.thork.app.pages.multi_asset

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityDetailsAssetBinding
import id.thork.app.databinding.ActivityListAssetBinding
import id.thork.app.pages.multi_asset.element.MultiAssetListAdapter
import id.thork.app.pages.multi_asset.element.MultiAssetViewModel

class DetailsAssetActivity : BaseActivity() {
    private val viewModels: MultiAssetViewModel by viewModels()
    private val binding: ActivityDetailsAssetBinding by binding(R.layout.activity_details_asset)

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@DetailsAssetActivity
            viewModel = viewModels
        }
        setupToolbarWithHomeNavigation(
            getString(R.string.detail_Asset),
            navigation = false,
            filter = false,
            scannerIcon = false
        )
        retriveFromIntent()
    }

    private fun retriveFromIntent(){
         val assetNum = intent.getStringExtra(BaseParam.ASSETNUM)
        assetNum.whatIfNotNullOrEmpty {
            viewModels.getMultiAssetByAssetNum(it)
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.getMultiAsset.observe(this, Observer {
            binding.apply {
                assetnum.text = it.assetNum
                assetDesc.text = it.description
                assetLocation.text = it.location
            }
        })
    }
}