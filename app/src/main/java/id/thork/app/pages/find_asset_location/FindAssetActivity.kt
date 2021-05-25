package id.thork.app.pages.find_asset_location

import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityFindAssetBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.attachment.AttachmentActivity
import id.thork.app.pages.find_asset_location.element.FindAssetAdapter
import id.thork.app.pages.find_asset_location.element.FindAssetViewModel
import id.thork.app.pages.multi_asset.element.MultiAssetListAdapter
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.AttachmentEntity
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named
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
        viewModels.findAllAsset()

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

//    private fun setUpFilterListener() {
//        binding.apply {
//            etFindAsset.addTextChangedListener(object : TextWatcher {
//                private var timer = Timer()
//                private val DELAY: Long = 1000 // milliseconds
//                var isTyping = false
//                override fun beforeTextChanged(
//                    s: CharSequence,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//                    //Method before text change
//                }
//
//                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                    //Method on text Change
//                }
//
//                override fun afterTextChanged(s: Editable) {
//                    if (!isTyping) {
//                        // Send notification for start typing event
//                        isTyping = true
//                    }
//                    timer.cancel()
//                    timer = Timer()
//                    timer.schedule(
//                        object : TimerTask() {
//                            override fun run() {
//                                isTyping = false
//                                activity?.runOnUiThread {
//                                    Timber.d("filter :%s", s.toString())
//                                    viewModel!!.searchWo(s.toString())
//                                }
//                            }
//                        }, DELAY
//                    )
//                }
//            })
//        }
//    }
}