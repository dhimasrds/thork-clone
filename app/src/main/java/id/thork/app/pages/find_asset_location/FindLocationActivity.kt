package id.thork.app.pages.find_asset_location

import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityFindLocationBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.find_asset_location.element.FindAssetViewModel
import id.thork.app.pages.find_asset_location.element.FindLocationAdapter
import id.thork.app.persistence.entity.LocationEntity
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FindLocationActivity: BaseActivity() {
    val TAG = FindAssetActivity::class.java.name
    private val viewModels: FindAssetViewModel by viewModels()
    private val binding: ActivityFindLocationBinding by binding(R.layout.activity_find_location)
    private lateinit var findLocationAdapter: FindLocationAdapter
    private lateinit var locationEntities : MutableList<LocationEntity>

    @Inject
    lateinit var requestOptions: RequestOptions

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@FindLocationActivity
            viewModel = viewModels
        }
        locationEntities = mutableListOf()
        findLocationAdapter = FindLocationAdapter(locationEntities, requestOptions, this, preferenceManager)

        binding.recyclerView.adapter = findLocationAdapter
        viewModels.getAllLocation()

        setupToolbarWithHomeNavigation(
            getString(R.string.find_location),
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
                findLocationAdapter.filter.filter(newText)
                return false
            }
        })
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.getLocationList.observe(this, Observer {
            locationEntities.clear()
            locationEntities.addAll(it)
            findLocationAdapter.notifyDataSetChanged()
            Timber.tag(TAG).d("setupObserver() size: %s", locationEntities.size)

        })
    }
}