package id.thork.app.pages

import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener
import id.thork.app.R
import id.thork.app.base.BaseActivity

/**
 * Created by Raka Putra on 3/4/21
 * Jakarta, Indonesia.
 */
class ScannerActivity : BaseActivity(), TorchListener {
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var switchFlashlightButton: LinearLayout
    private lateinit var backButton: ImageView
    private var capture: CaptureManager? = null
    private var imgFlash: ImageView? = null
    private var txtFlash: TextView? = null
    private var isFlashLightOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        //Initialize barcode scanner view
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner)
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { finish() }

        //set torch listener
//        barcodeScannerView.setTorchListener(this);
        barcodeScannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {}
            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        })


        //switch flashlight button
        txtFlash = findViewById(R.id.txt_flash)
        imgFlash = findViewById(R.id.img_flash)
        switchFlashlightButton = findViewById(R.id.switch_flashlight)

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE)
        } else {
            switchFlashlightButton.setOnClickListener(View.OnClickListener { switchFlashlight() })
        }

        //start capture
        capture = CaptureManager(this, barcodeScannerView)
        capture!!.initializeFromIntent(intent, savedInstanceState)
        capture!!.decode()
    }

    override fun onTorchOn() {
        imgFlash!!.setImageDrawable(resources.getDrawable(R.drawable.ic_flash_off))
        txtFlash!!.text = resources.getString(R.string.flash_off)
    }

    override fun onTorchOff() {
        imgFlash!!.setImageDrawable(resources.getDrawable(R.drawable.ic_flash))
        txtFlash!!.text = resources.getString(R.string.flash_on)
    }

    fun switchFlashlight() {
        isFlashLightOn = if (isFlashLightOn) {
            barcodeScannerView!!.setTorchOff()
            false
        } else {
            barcodeScannerView!!.setTorchOn()
            true
        }
    }

    /**
     * Check if the device's camera has a Flashlight.
     *
     * @return true if there is Flashlight, otherwise false.
     */
    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    override fun onResume() {
        super.onResume()
        capture!!.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState!!)
        capture!!.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeScannerView!!.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.size < 1) {}
        else {
            barcodeScannerView!!.resume()
        }
    }
}