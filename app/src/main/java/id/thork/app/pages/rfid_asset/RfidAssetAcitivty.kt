package id.thork.app.pages.rfid_asset

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.skydoves.whatif.whatIfNotNull
import com.zebra.rfid.api3.TagData
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityRfidAssetBinding
import id.thork.app.helper.rfid.RFIDHandler
import id.thork.app.pages.rfid_asset.element.RfidAssetActivityViewModel
import timber.log.Timber

class RfidAssetAcitivty : BaseActivity(), RFIDHandler.ResponseHandlerInterface {
    private val TAG = RfidAssetAcitivty::class.java.name

    private val rfidAssetViewModel: RfidAssetActivityViewModel by viewModels()
    private val binding: ActivityRfidAssetBinding by binding(R.layout.activity_rfid_asset)
    private var assetIsMatch = false


    /**
     * RFID Handler
     */
    private var rfidHandler: RFIDHandler? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@RfidAssetAcitivty
            vm = rfidAssetViewModel
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.scan_asset),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )

        setupRfid()
        retrieveFromIntent()
    }

    private fun setupRfid() {
        if (rfidHandler == null) {
            rfidHandler = RFIDHandler()
            rfidHandler?.init(this, true, this)
        }
    }

    private fun retrieveFromIntent() {
        //000000000000000000001313
        val intentAssetnum = intent.getStringExtra(BaseParam.RFID_ASSETNUM)
        Timber.d("retrieveFromIntent() %s", intentAssetnum)
        intentAssetnum.whatIfNotNull {
            //TODO Query to local
            rfidAssetViewModel.initAsset(it)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setupObserver() {
        super.setupObserver()
        rfidAssetViewModel.assetEntity.observe(this, {
            rfidHandler?.setTagId(it.assetrfid)
            binding.tvAssetNum.text = it.assetnum
            binding.tvAssetLocation.text = it.description
        })

        rfidAssetViewModel.percentageResult.observe(this, {
            val percentage = it + "% " + getString(R.string.asset_rfid_is_match)
            binding.tvAssetResult.setText(
                """${getString(R.string.asset_rfid_is_match_begin)} $percentage
${getString(R.string.asset_rfid_is_match_end)}"""
            )
        })

        rfidAssetViewModel.result.observe(this, {
            if (it == BaseParam.APP_TRUE) {
                binding.tvAssetResult.visibility = View.VISIBLE
                binding.layoutAction.visibility = View.VISIBLE
                assetIsMatch = true
            } else {
                binding.tvAssetResult.visibility = View.GONE
                binding.layoutAction.visibility = View.GONE
                assetIsMatch = false
            }
        })
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnContinue.setOnClickListener {
            val intent = Intent()
            intent.putExtra(BaseParam.RFID_ASSET_IS_MATCH, assetIsMatch)
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.btnRetry.setOnClickListener {
            binding.percentChartView.setProgress(0f, true)
            binding.tvAssetResult.visibility = View.GONE
            binding.layoutAction.visibility = View.GONE
            assetIsMatch = false
        }
    }

    override fun onPause() {
        super.onPause()
        rfidHandler?.onPause()
    }

    override fun onPostResume() {
        super.onPostResume()
        rfidHandler?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        rfidHandler?.onDestroy()
    }

    override fun onConnected(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDisconnected() {
        Toast.makeText(this, "disconnect", Toast.LENGTH_LONG).show()
    }

    override fun handleTagdata(tagData: Array<out TagData>?) {
        val sb = StringBuilder()
        for (index in tagData!!.indices) {
            sb.append(
                """
            ${tagData[index].tagID}
            
            """.trimIndent()
            )
        }
        runOnUiThread { Timber.tag(TAG).d("handleTagdata() tag data: %s", sb.toString()) }
    }

    override fun handleTriggerPress(pressed: Boolean) {
        if (pressed) {
            rfidHandler!!.performLocation()
        } else {
            rfidHandler!!.stopInventory()
        }
    }

    override fun handleLocationData(distance: Short?) {
        try {
            runOnUiThread {
                binding.percentChartView.setProgress(distance!!.toFloat(), true)
                Timber.tag(TAG).d("handleLocationData() distance: %s", distance)
                rfidAssetViewModel.showAssetRfidResult(distance.toInt())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}