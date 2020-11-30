package id.thork.app.pages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.widget.TextView
import androidx.viewbinding.BuildConfig
import id.thork.app.BuildConfig
import id.thork.app.R
import id.thork.app.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initVersion()

//        if(){

//        }else {

            val thread: Thread = object : Thread() {
                override fun run() {
                    try {
                        sleep(1200)
                        val intent: Intent
                        intent = if (preferenceManager!!.getBoolean(BaseParam.FIRST_LAUNCH)) {
                            Intent(applicationContext, IntroActivity::class.java)
                        } else {
                            Intent(applicationContext, IpConfigActivity::class.java)
                        }
                        //get active user language
                        if (appSession!!.user != null) {

                            startActivity(intent)
                            finish()
                        }
                        else {
                            startActivity(intent)
                            finish()
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
            thread.start()

    }

    fun initVersion(){
        val versionName = LIBRARY_PACKAGE_NAME
        binding.txtVersion.setText(versionName)
    }

    override fun onResume() {
        super.onResume()
    }
}