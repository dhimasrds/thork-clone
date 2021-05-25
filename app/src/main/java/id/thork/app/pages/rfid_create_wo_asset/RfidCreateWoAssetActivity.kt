package id.thork.app.pages.rfid_create_wo_asset

import android.widget.Toast
import androidx.activity.viewModels
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.zebra.rfid.api3.TagData
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityRfidCreateWoAssetBinding
import id.thork.app.helper.rfid.RFIDHandler
import id.thork.app.pages.rfid_create_wo_asset.element.RfidCreateWoAssetActivityViewModel
import timber.log.Timber

class RfidCreateWoAssetActivity : BaseActivity(), RFIDHandler.ResponseHandlerInterface {
    private val TAG = RfidCreateWoAssetActivity::class.java.name

    private val rfidCreateWoAssetViewModel: RfidCreateWoAssetActivityViewModel by viewModels()
    private val binding: ActivityRfidCreateWoAssetBinding by binding(R.layout.activity_rfid_create_wo_asset)

    /**
     * RFID Handler
     */
    private var rfidHandler: RFIDHandler? = null


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@RfidCreateWoAssetActivity
            vm = rfidCreateWoAssetViewModel
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
    }

    private fun setupRfid() {
        if (rfidHandler == null) {
            rfidHandler = RFIDHandler()
            rfidHandler?.init(this, false, this)
        }
    }

    override fun setupObserver() {
        super.setupObserver()
    }

    override fun setupListener() {
        super.setupListener()
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
        tagData.whatIfNotNullOrEmpty {
            val sb: StringBuilder = StringBuilder()
            for (i in 0..(it.size - 1)) {
                sb.append(it[i].tagID + "\n")
            }
            runOnUiThread {
                Timber.tag(TAG).d("handleTagdata() tag data: %s", sb.toString())
            }
        }
    }

    override fun handleTriggerPress(pressed: Boolean) {
        if (pressed) {
            rfidHandler!!.performInventory()
        } else {
            rfidHandler!!.stopInventory()
        }    }

    override fun handleLocationData(distance: Short?) {
        TODO("Not yet implemented")
    }


}