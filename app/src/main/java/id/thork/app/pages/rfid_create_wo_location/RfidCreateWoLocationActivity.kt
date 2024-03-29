package id.thork.app.pages.rfid_create_wo_location

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.zebra.rfid.api3.TagData
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityRfidCreateWoLocationBinding
import id.thork.app.helper.rfid.RFIDHandler
import id.thork.app.pages.rfid_create_wo_location.element.RfidCreateWoLocationActivityViewModel
import id.thork.app.utils.CommonUtils
import timber.log.Timber

class RfidCreateWoLocationActivity : BaseActivity(), RFIDHandler.ResponseHandlerInterface {
    private val TAG = RfidCreateWoLocationActivity::class.java.name

    private val rfidCreateWoAssetActivityViewModel: RfidCreateWoLocationActivityViewModel by viewModels()
    private val binding: ActivityRfidCreateWoLocationBinding by binding(R.layout.activity_rfid_create_wo_location)

    /**
     * RFID Handler
     */
    private var rfidHandler: RFIDHandler? = null

    private var tempTagcode: String? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@RfidCreateWoLocationActivity
            vm = rfidCreateWoAssetActivityViewModel
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.scan_location),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
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
        rfidCreateWoAssetActivityViewModel.locationCache.observe(this, Observer {
            binding.tvLocation.text = it.location
            binding.layoutAction.visibility = View.VISIBLE
        })
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnContinue.setOnClickListener {
            val intent = Intent()
            intent.putExtra(BaseParam.LOCATIONS, binding.tvLocation.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.btnRetry.setOnClickListener {
            binding.tvLocation.text = BaseParam.APP_EMPTY_STRING
            binding.layoutAction.visibility = View.GONE
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
        CommonUtils.standardToast(message.toString())

    }

    override fun onDisconnected() {
        CommonUtils.standardToast("Disconnect")
    }

    override fun handleTagdata(tagData: Array<out TagData>?) {
        tagData.whatIfNotNullOrEmpty {
            val sb: StringBuilder = StringBuilder()
            for (i in 0..(it.size - 1)) {
                sb.append(it[i].tagID)
            }
            runOnUiThread {
                Timber.tag(TAG).d("handleTagdata() tag data: %s", sb.toString())
                tempTagcode = sb.toString()
            }
        }
    }

    override fun handleTriggerPress(pressed: Boolean) {
        if (pressed) {
            rfidHandler!!.performInventory()
        } else {
            rfidCreateWoAssetActivityViewModel.checkLocationTagcode(tempTagcode.toString())
            rfidHandler!!.stopInventory()
        }
    }

    override fun handleLocationData(distance: Short?) {
        TODO("Not yet implemented")
    }


}