/*
 * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * This.ID. ("Confidential Information").
 *
 * Such Confidential Information shall not be disclosed and shall
 * use it only	 in accordance with the terms of the license agreement
 * entered into with This.ID; other than in accordance with the written
 * permission of This.ID.
 */

package id.thork.app.pages.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.zebra.rfid.api3.TagData
import id.thork.app.R
import id.thork.app.helper.rfid.RFIDHandler
import id.thork.app.pages.settings.SettingsActivity
import timber.log.Timber

class RfidExActivity : AppCompatActivity(), RFIDHandler.ResponseHandlerInterface {
    private val TAG = RfidExActivity::class.java.name

    /**
     * RFID Handler
     */
    private var rfidHandler: RFIDHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rfid_ex)

        setupRfid()
    }

    private fun setupRfid() {
        if (rfidHandler == null) {
            rfidHandler = RFIDHandler()
            rfidHandler?.init(this, true, this)
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
            rfidHandler?.performInventory()
        } else {
            rfidHandler?.stopInventory()
        }
    }

    override fun handleLocationData(distance: Short?) {
        Timber.tag(TAG).d("handleLocationData() distance: %s", distance)
    }
}