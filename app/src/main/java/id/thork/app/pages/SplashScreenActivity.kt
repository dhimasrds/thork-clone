package id.thork.app.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.thork.app.BuildConfig
import id.thork.app.R
import id.thork.app.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    private fun initVersion(){
        var versionName : String = BuildConfig.VERSION_NAME
    }
}