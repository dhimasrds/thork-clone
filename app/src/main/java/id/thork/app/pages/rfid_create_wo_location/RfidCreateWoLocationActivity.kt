package id.thork.app.pages.rfid_create_wo_location

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.pages.rfid_create_wo_asset.RfidCreateWoAssetActivity

class RfidCreateWoLocationActivity : BaseActivity() {
    private val TAG = RfidCreateWoLocationActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rfid_create_wo_location)
    }


}