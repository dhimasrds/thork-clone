package id.thork.app.pages.find_asset_location

import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityFindAssetBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.find_asset_location.element.FindAssetAdapter
import id.thork.app.pages.find_asset_location.element.FindAssetViewModel
import id.thork.app.persistence.entity.AssetEntity
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FindAssetActivity : BaseActivity() {
    val TAG = FindAssetActivity::class.java.name
    private val viewModels: FindAssetViewModel by viewModels()
    private val binding: ActivityFindAssetBinding by binding(R.layout.activity_find_asset)
    private lateinit var findAssetAdapter: FindAssetAdapter
    private lateinit var assetEntities: MutableList<AssetEntity>

    @Inject
    lateinit var requestOptions: RequestOptions

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@FindAssetActivity
            viewModel =viewModels
        }
        assetEntities = mutableListOf()
        findAssetAdapter = FindAssetAdapter(assetEntities,requestOptions,this,preferenceManager)

        binding.recyclerView.adapter = findAssetAdapter
        viewModels.getAllAsset()

        setupToolbarWithHomeNavigation(
            getString(R.string.find_asset),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )

        binding.etFindAsset.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                findAssetAdapter.filter.filter(newText)
                return false
            }

        })
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