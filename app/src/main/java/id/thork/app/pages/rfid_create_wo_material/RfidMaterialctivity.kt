package id.thork.app.pages.rfid_create_wo_material

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.zebra.rfid.api3.TagData
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityRfidMaterialctivityBinding
import id.thork.app.helper.rfid.RFIDHandler
import id.thork.app.pages.rfid_create_wo_material.element.RfidMaterialActivityViewModel
import id.thork.app.utils.CommonUtils
import timber.log.Timber

class RfidMaterialctivity : BaseActivity(), RFIDHandler.ResponseHandlerInterface {
    private val TAG = RfidMaterialctivity::class.java.name
    private val viewModel: RfidMaterialActivityViewModel by viewModels()
    private val binding: ActivityRfidMaterialctivityBinding by binding(R.layout.activity_rfid_materialctivity)

    /**
     * RFID Handler
     */
    private var rfidHandler: RFIDHandler? = null

    private var tempTagcode: String? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@RfidMaterialctivity
            vm = viewModel
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.wo_scan_material),
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

    override fun setupListener() {
        super.setupListener()
        binding.btnContinue.setOnClickListener {
            val intent = Intent()
            intent.putExtra(BaseParam.MATERIAL, binding.tvMaterial.text.toString())
            intent.putExtra(BaseParam.DESCRIPTION, binding.tvDescription.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.btnRetry.setOnClickListener {
            binding.tvMaterial.text = BaseParam.APP_EMPTY_STRING
            binding.tvDescription.text = BaseParam.APP_EMPTY_STRING
            binding.layoutAction.visibility = View.GONE
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.materialCache.observe(this, Observer {
            binding.tvMaterial.text = it.itemNum
            binding.tvDescription.text = it.description
            binding.layoutAction.visibility = View.VISIBLE
        })
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
                //Check to local cache
                tempTagcode = sb.toString()
            }
        }
    }

    override fun handleTriggerPress(pressed: Boolean) {
        if (pressed) {
            rfidHandler!!.performInventory()
        } else {
            viewModel.checkMaterialTagCode(tempTagcode.toString())
            rfidHandler!!.stopInventory()
        }
    }

    override fun handleLocationData(distance: Short?) {
        TODO("Not yet implemented")
    }


}