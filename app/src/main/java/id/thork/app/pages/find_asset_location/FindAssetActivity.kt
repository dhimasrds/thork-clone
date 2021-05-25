package id.thork.app.pages.find_asset_location

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityFindAssetBinding
import id.thork.app.pages.attachment.AttachmentActivity
import id.thork.app.pages.find_asset_location.element.FindAssetAdapter
import id.thork.app.pages.find_asset_location.element.FindAssetViewModel
import id.thork.app.pages.multi_asset.element.MultiAssetListAdapter
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.AttachmentEntity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class FindAssetActivity : BaseActivity() {
    val TAG = FindAssetActivity::class.java.name
    private val viewModels: FindAssetViewModel by viewModels()
    private val binding: ActivityFindAssetBinding by binding(R.layout.activity_find_asset)
    private lateinit var findAssetAdapter: FindAssetAdapter
    private lateinit var assetEntities: MutableList<AssetEntity>

    @Inject
    lateinit var requestOptions: RequestOptions

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@FindAssetActivity
            viewModel =viewModels
        }
        assetEntities = mutableListOf()
        findAssetAdapter = FindAssetAdapter(assetEntities,requestOptions)

        binding.recyclerView.adapter = findAssetAdapter
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

    override fun setupObserver() {
        super.setupObserver()
        viewModels.getFindAssetList.observe(this, Observer {
            assetEntities.clear()
            assetEntities.addAll(it)
            findAssetAdapter.notifyDataSetChanged()
            Timber.tag(TAG).d("setupObserver() size: %s", assetEntities.size)

        })
    }
}