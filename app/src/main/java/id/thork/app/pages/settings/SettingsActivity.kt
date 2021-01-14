package id.thork.app.pages.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivitySettingsBinding
import id.thork.app.pages.about.AboutActivity
import id.thork.app.pages.settings.element.SettingsViewModel
import id.thork.app.pages.settings_language.SettingsLanguageActivity
import id.thork.app.pages.settings_log.LogActivity
import id.thork.app.pages.splash_screen.element.SplashState

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class SettingsActivity : BaseActivity() {

    private val viewModel: SettingsViewModel by viewModels()
    private val binding: ActivitySettingsBinding by binding(R.layout.activity_settings)

    private val TAG = SettingsActivity::class.java.name
    private val TAG_SWITCH_PATTERN = "TAG_SWITCH_PATTERN"
    private val TAG_CHANGE_PATTERN = "TAG_CHANGE_PATTERN"
    private val TAG_CLEAR_CACHE = "CLEAR_CACHE"
    private val TAG_LOGOUT = "TAG_LOGOUT"
    private val TAG_SETTING = "TAG_SETTING"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handlerOnclick()
    }

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@SettingsActivity
            vm = viewModel
        }
    }

    private fun handlerOnclick() {
        binding.settingsAbout.setOnClickListener {
            goToAboutActivity()
        }

        binding.settingsLogs.setOnClickListener {
            goToLogActivity()
        }

        binding.settingsLanguage.setOnClickListener {
            goToLanguageActivity()
        }

        binding.settingsChangePattern.setOnClickListener {
            goToLoginPatternActivity()
        }
    }

    private fun goToLoginPatternActivity() {
        finish()
        startActivity(Intent(this, SplashState.LoginPatternActivity::class.java))
    }

    private fun goToLanguageActivity() {
        finish()
        startActivity(Intent(this, SettingsLanguageActivity::class.java))
    }

    private fun goToLogActivity() {
        finish()
        startActivity(Intent(this, LogActivity::class.java))
    }

    private fun goToAboutActivity() {
        finish()
        startActivity(Intent(this, AboutActivity::class.java))
    }

}