package id.thork.app.pages.multi_asset

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.zxing.integration.android.IntentIntegrator
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityListAssetBinding
import id.thork.app.pages.multi_asset.element.MultiAssetListAdapter
import id.thork.app.pages.multi_asset.element.MultiAssetViewModel
import timber.log.Timber

class ListAssetActivity : BaseActivity() {
    private val viewModels: MultiAssetViewModel by viewModels()
    private val binding: ActivityListAssetBinding by binding(R.layout.activity_list_asset)
    private lateinit var multiAssetAdapter: MultiAssetListAdapter
    var intentWonum: String? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@ListAssetActivity
            viewModel = viewModels
        }
        multiAssetAdapter = MultiAssetListAdapter()
        binding.recyclerView.adapter = multiAssetAdapter

        setupToolbarWithHomeNavigation(
            getString(R.string.list_of_assets),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )
        retriveFromIntent()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.getMultiAssetList.observe(this, Observer {
            multiAssetAdapter.setMultiAssetList(it, this)
        })
    }

    override fun goToPreviousActivity() {
        super.goToPreviousActivity()
        finish()
    }

    private fun retriveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWonum.whatIfNotNullOrEmpty {
            viewModels.getMultiAssetByParent(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
                    BaseParam.RFID_REQUEST_CODE_MULTIASSET -> {
                        //TODO update local cache after tag rfid
                        data.whatIfNotNull {
                            val intentAssetnum = it.getStringExtra(BaseParam.RFID_ASSETNUM)
                            val multiAssetIsMatch =
                                it.getBooleanExtra(BaseParam.RFID_ASSET_IS_MATCH, false)
                            Timber.d("retrieveFromIntent() assetnum %s", intentAssetnum)
                            Timber.d("retrieveFromIntent() wonum %s", intentWonum)
                            Timber.d("retrieveFromIntent() multiAssetIsMatch %s", multiAssetIsMatch)
                        }
                        multiAssetAdapter.notifyDataSetChanged()
                    }

                    BaseParam.BARCODE_REQUEST_CODE_MULTIASSET -> {
                        //TODO update local cache after scan barcode
                        result.whatIfNotNull {
                            Timber.d("retrieveFromIntent() result barcode %s", it.contents)
                        }
                        multiAssetAdapter.notifyDataSetChanged()

                    }
                }
            }
        }

    }


}