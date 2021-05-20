package id.thork.app.pages.rfid_location

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import com.zebra.rfid.api3.TagData
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityRfidLocationBinding
import id.thork.app.helper.rfid.RFIDHandler
import id.thork.app.pages.rfid_location.element.RfidLocationActivityViewModel
import timber.log.Timber

class RfidLocationActivity : BaseActivity(), RFIDHandler.ResponseHandlerInterface {
    private val TAG = RfidLocationActivity::class.java.name

    private val rfidLocationViewModel: RfidLocationActivityViewModel by viewModels()
    private val binding: ActivityRfidLocationBinding by binding(R.layout.activity_rfid_location)
    private var locationIsMatch = false

    /**
     * RFID Handler
     */
    private var rfidHandler: RFIDHandler? = null


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@RfidLocationActivity
            vm = rfidLocationViewModel
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.scan_asset),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
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
        val intentLocation = intent.getStringExtra(BaseParam.RFID_LOCATION)
        Timber.d("retrieveFromIntent() %s", intentLocation)
        intentLocation.whatIfNotNull {
            //TODO Query to local
            rfidLocationViewModel.initLocation(it)
        }
    }



    @SuppressLint("SetTextI18n")
    override fun setupObserver() {
        super.setupObserver()
        rfidLocationViewModel.locationEntity.observe(this, Observer {
            Timber.d("setupObserver() location %s", it.location)
            rfidHandler?.setTagId(it.thisfsmrfid)
            binding.tvLocation.text = it.location
            binding.tvLocationDescription.text = it.description
        })

        rfidLocationViewModel.percentageResult.observe(this, Observer {
            val percentage = it + "% " + getString(R.string.asset_rfid_is_match)
            binding.tvAssetResult.setText(
                """${getString(R.string.asset_rfid_is_match_begin)} $percentage
${getString(R.string.asset_rfid_is_match_end)}"""
            )
        })

        rfidLocationViewModel.result.observe(this, Observer {
            if (it == BaseParam.APP_TRUE) {
                binding.tvAssetResult.visibility = View.VISIBLE
                binding.layoutAction.visibility = View.VISIBLE
                locationIsMatch = true
            } else {
                binding.tvAssetResult.visibility = View.GONE
                binding.layoutAction.visibility = View.GONE
                locationIsMatch = false
            }
        })
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnContinue.setOnClickListener {
            val intent = Intent()
            intent.putExtra(BaseParam.RFID_LOCATION_IS_MATCH, locationIsMatch)
            setResult(RESULT_OK, intent)
            finish()
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
        Toast.makeText(this, "disconnect", Toast.LENGTH_LONG).show();
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
                rfidLocationViewModel.showAssetRfidResult(distance.toInt())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}