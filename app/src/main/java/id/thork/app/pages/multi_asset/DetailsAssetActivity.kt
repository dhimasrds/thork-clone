package id.thork.app.pages.multi_asset

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.google.zxing.integration.android.IntentIntegrator
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityDetailsAssetBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.GlideApp
import id.thork.app.pages.ScannerActivity
import id.thork.app.pages.multi_asset.element.MultiAssetViewModel
import id.thork.app.pages.rfid_mutli_asset.RfidMultiAssetActivity
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DetailsAssetActivity : BaseActivity() {
    private val viewModels: MultiAssetViewModel by viewModels()
    private val binding: ActivityDetailsAssetBinding by binding(R.layout.activity_details_asset)
    var intentAssetNum: String? = null
    var intentWonum: String? = null
    private var assetIsMatch = false

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
            option = false,
            historyAttendanceIcon = false
        )
        retriveFromIntent()
    }

    private fun retriveFromIntent() {
        intentAssetNum = intent.getStringExtra(BaseParam.ASSETNUM)
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        Timber.d("retrieveFromIntent() %s", intentAssetNum)
        Timber.d("retrieveFromIntent() %s", intentWonum)

        intentAssetNum.whatIfNotNullOrEmpty { assetnum ->
            intentWonum.whatIfNotNull { wonum ->
                viewModels.getMultiAssetByAssetNum(assetnum, wonum)

            }
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

        viewModels.result.observe(this, Observer {
            if (it.equals(BaseParam.APP_TRUE)) {
                navigateToListAsset()
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

    override fun goToPreviousActivity() {
        super.goToPreviousActivity()
        navigateToListAsset()
    }

    override fun setupListener() {
        super.setupListener()

        binding.buttonRfid.setOnClickListener {
            navigateToRfid()
        }

        binding.buttonQr.setOnClickListener {
            startQRScanner(BaseParam.BARCODE_REQUEST_CODE_DETAIL_MULTI_ASSET)
        }
    }

    private fun navigateToRfid() {
        val intent = Intent(this, RfidMultiAssetActivity::class.java)
        intent.putExtra(BaseParam.RFID_ASSETNUM, intentAssetNum)
        intent.putExtra(BaseParam.WONUM, intentWonum)
        startActivityForResult(intent, BaseParam.RFID_REQUEST_CODE_DETAIL_MULTI_ASSET)
    }


    private fun startQRScanner(requestCode: Int) {
        IntentIntegrator(this).apply {
            setCaptureActivity(ScannerActivity::class.java)
            setRequestCode(requestCode)
            initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d(
            "retrieveFromIntent() onActivityResult data %s resultCode %s requestcode %s ",
            data,
            resultCode,
            requestCode
        )
        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
                    BaseParam.RFID_REQUEST_CODE_DETAIL_MULTI_ASSET -> {
                        //TODO
                        data.whatIfNotNull {
                            Timber.d("retrieveFromIntent() after scan rfid %s", data)
                            val multiAssetIsMatch =
                                it.getBooleanExtra(BaseParam.RFID_ASSET_IS_MATCH, false)
                            if (multiAssetIsMatch) {
                                assetIsMatch = multiAssetIsMatch
                                viewModels.updateMultiAsset(
                                    intentAssetNum.toString(),
                                    intentWonum.toString(),
                                    BaseParam.APP_TRUE,
                                    BaseParam.SCAN_TYPE_RFID
                                )
                            }
                        }
                    }

                    BaseParam.BARCODE_REQUEST_CODE_DETAIL_MULTI_ASSET -> {
                        //TODO
                        if (intentAssetNum.equals(result.contents)) {
                            viewModels.updateMultiAsset(
                                intentAssetNum.toString(),
                                intentWonum.toString(),
                                BaseParam.APP_TRUE,
                                BaseParam.SCAN_TYPE_BARCODE
                            )
                        }
                    }
                }
            }
        }
    }

    private fun navigateToListAsset() {
        Timber.d("retrieveFromIntent() navigate to list asset ")
        val intent = Intent(this, ListAssetActivity::class.java)
        intent.putExtra(BaseParam.WONUM, intentWonum)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToListAsset()
    }
}