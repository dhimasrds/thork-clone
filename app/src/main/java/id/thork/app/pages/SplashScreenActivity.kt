package id.thork.app.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.thork.app.R

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
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


    override fun onResume() {
        super.onResume()
    }

    private fun initVersion(){
        var versionName : String = BuildConfig.VERSION_NAME
    }
}