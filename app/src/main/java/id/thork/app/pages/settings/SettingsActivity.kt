package id.thork.app.pages.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivitySettingsBinding
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.about.AboutActivity
import id.thork.app.pages.settings.element.SettingsViewModel
import id.thork.app.pages.settings_language.SettingsLanguageActivity
import id.thork.app.pages.settings_log.LogActivity
import id.thork.app.pages.splash_screen.element.SplashState

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class SettingsActivity : BaseActivity(), DialogUtils.DialogUtilsListener {

    private val viewModel: SettingsViewModel by viewModels()
    private val binding: ActivitySettingsBinding by binding(R.layout.activity_settings)

    private val TAG = SettingsActivity::class.java.name
    private val TAG_SWITCH_PATTERN = "TAG_SWITCH_PATTERN"
    private val TAG_CHANGE_PATTERN = "TAG_CHANGE_PATTERN"
    private val TAG_CLEAR_CACHE = "CLEAR_CACHE"
    private val TAG_LOGOUT = "TAG_LOGOUT"
    private val TAG_SETTING = "TAG_SETTING"
    private var currentTag: String = ""
    private var dialogLogout: DialogUtils? = null
    private var dialogChangePattern: DialogUtils? = null
    private var dialogSwitchPattern: DialogUtils? = null
    private var dialogCache: DialogUtils? = null


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

        binding.buttonLogout.setOnClickListener {

            //Logout User Session
            currentTag = TAG_LOGOUT
            dialogLogout = DialogUtils(this)
            dialogLogout!!
                    .setTitles(R.string.logout_title)
                    .setMessage(R.string.logout_qustion)
                    .setPositiveButtonLabel(R.string.dialog_yes)
                    .setNegativeButtonLabel(R.string.dialog_no)
                    .setListener(this)
                    .show()
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

    override fun onPositiveButton() {
        when (currentTag) {
            TAG_LOGOUT -> logoutPostCrew()
//                var  intent: Intent? = Intent(this@SettingsActivity, ServerActivity::class.java)
//            if (appSession.getUser() != null) {
//                appSession.getUser().getLanguage()
//                appSession.getUser().getDarkMode()
//                LocaleHelper.setLocale(this, BaseParam.DEFAULT_LANG)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                settingsPresenter.setSessionEnded()
//            }
//                    startActivity intent
        }
    }

    override fun onNegativeButton() {

    }

    fun logoutPostCrew() {
//        val mFusedLocation: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        mFusedLocation.getLastLocation().addOnSuccessListener(this) { location ->
//            loginId = Integer.toString(appSession.getPersonUID())
//            settingsPresenter.postCrewPosition(loginId, laborcode, location.getLatitude().toString() + "", location.getLongitude().toString() + "", tag)
//        }
    }
}