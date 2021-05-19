package id.thork.app.pages.rfid_location

import android.os.Bundle
import id.thork.app.R
import id.thork.app.base.BaseActivity

class RfidLocationActivity : BaseActivity() {
    private val TAG = RfidLocationActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rfid_location)
    }
}