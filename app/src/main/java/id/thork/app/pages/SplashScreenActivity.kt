package id.thork.app.pages

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.thork.app.BuildConfig
import id.thork.app.R
import id.thork.app.databinding.ActivitySplashScreenBinding
import id.thork.app.pages.server.ServerActivity

class SplashScreenActivity : AppCompatActivity(),DialogUtils.DialogUtilsListener {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initVersion()

        val rootUtils = RootUtils(this)
        if(rootUtils.isRootedWithoutTestKey){
            //we found indication of root
            showDialogExit()

        }else {

            val thread: Thread = object : Thread() {
                override fun run() {
                    try {
                        sleep(1200)
                        val intent = Intent(applicationContext, ServerActivity::class.java)
//                        intent = if () {
//                            Intent(applicationContext, IntroActivity::class.java)
//                        } else {
//                            Intent(applicationContext, IpConfigActivity::class.java)
//                        }
                        //get active user language
//                        if () {

//                        }
//                        else {
                        startActivity(intent)
                        finish()
//                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
            thread.start()
        }
    }

    private fun initVersion(){
        var versionName : String = BuildConfig.VERSION_NAME
        binding.txtVersion.text = versionName
    }

    override fun onResume() {
        super.onResume()
    }

    private fun showDialogExit() {
        val dialogExit = DialogUtils(this)
        dialogExit
            .setTitles(R.string.dialog_root_title)
            .setMessage(R.string.dialog_root_subtitle)
            .setPositiveButtonLabel(R.string.dialog_yes)
            .setListener(this)
            .show()
    }

    override fun onPositiveButton() {
        finish()
    }

    override fun onNegativeButton() {
        TODO("Not yet implemented")
    }


}