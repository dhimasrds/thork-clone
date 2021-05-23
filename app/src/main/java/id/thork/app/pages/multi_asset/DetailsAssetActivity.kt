package id.thork.app.pages.multi_asset

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.skydoves.whatif.whatIfNotNullOrEmpty
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityDetailsAssetBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.GlideApp
import id.thork.app.pages.multi_asset.element.MultiAssetViewModel
import javax.inject.Inject

@AndroidEntryPoint
class DetailsAssetActivity : BaseActivity() {
    private val viewModels: MultiAssetViewModel by viewModels()
    private val binding: ActivityDetailsAssetBinding by binding(R.layout.activity_details_asset)

    @Inject
    lateinit var requestOptions: RequestOptions

    @Inject
    lateinit var preferenceManager: PreferenceManager

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
            scannerIcon = false,
            notification = false,
            option = false
        )
        retriveFromIntent()
    }

    private fun retriveFromIntent() {
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
                loadImageHttps(it.image.toString())
            }
        })
    }

    private fun loadImageHttps(imageUrl: String) {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val glideUrl = GlideUrl(
            imageUrl, LazyHeaders.Builder()
                .addHeader("Cookie", cookie)
                .build()
        )
        GlideApp.with(this).load(glideUrl)
            .apply(requestOptions)
            .into(binding.ivAsset)
    }
}