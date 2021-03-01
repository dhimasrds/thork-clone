package id.thork.app.pages.settings

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseParam.TAG_SETTING
import id.thork.app.databinding.ActivitySettingsBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.about.AboutActivity
import id.thork.app.pages.login_pattern.LoginPatternActivity
import id.thork.app.pages.main.MainActivity
import id.thork.app.pages.server.ServerActivity
import id.thork.app.pages.settings.element.SettingsViewModel
import id.thork.app.pages.settings_language.SettingsLanguageActivity
import id.thork.app.pages.settings_log.LogActivity
import id.thork.app.pages.settings_pattern.SettingsPatternActivity
import id.thork.app.utils.LocaleHelper

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class SettingsActivity : BaseActivity(), CustomDialogUtils.DialogActionListener {
    private val TAG = SettingsActivity::class.java.name
    private val viewModel: SettingsViewModel by viewModels()
    private val binding: ActivitySettingsBinding by binding(R.layout.activity_settings)
    private val SETTINGS_REQUEST_CODE = 0

    private lateinit var customDialogUtils: CustomDialogUtils
    private val TAG_SWITCH_PATTERN = "TAG_SWITCH_PATTERN"
    private val TAG_CHANGE_PATTERN = "TAG_CHANGE_PATTERN"
    private val TAG_ACTIVE_PATTERN = "TAG_ACTIVE_PATTERN"
    private val TAG_LOGOUT = "TAG_LOGOUT"
    private var currentTag: String = ""


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
        customDialogUtils = CustomDialogUtils(this)

        setupToolbarWithHomeNavigation(getString(R.string.action_settings), navigation = false)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.validateLanguage()
        viewModel.selectedLang.observe(this, Observer {
            binding.settingsLanguageTitle.text = it
        })
        viewModel.validateUsername()
        viewModel.username.observe(this, Observer {
            binding.usernameId.text = it
        })
        viewModel.validateLogin()
        viewModel.isPattern.observe(this, Observer {
            binding.activatePattern.isChecked = it == BaseParam.APP_TRUE
        })
        viewModel.logout.observe(this, {
            if (it == BaseParam.APP_TRUE) {
                goToServerAcitivity()
            }
        })
    }

    private fun handlerOnclick() {
        binding.settingsAbout.setOnClickListener {
            goToAboutActivity()
        }

        binding.settingsLanguage.setOnClickListener {
            goToLanguageActivity()
        }

        binding.settingsChangePattern.setOnClickListener {
            setDialogChangePattern()
        }

        binding.activatePattern.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            activatePatternPassword(b)
            if (b == true) {
                viewModel.validatePattern()
                viewModel.pattern.observe(this, Observer {
                    if (it == null) {
                        setDialogSwitchPattern()
                    }
                })
            }
        })

        binding.settingsLogs.setOnClickListener {
            goToLogActivity()
        }

        binding.buttonLogout.setOnClickListener {
            setDialogLogout()
        }
    }

    private fun activatePatternPassword(b: Boolean) {
        val value = if (b) BaseParam.APP_TRUE else BaseParam.APP_FALSE
        viewModel.setUserIsPattern(value)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            LocaleHelper.setLocale(
                this@SettingsActivity,
                data.getStringExtra(BaseParam.SELECTED_LANG_CODE)
            )
        }
        startActivity(intent)
        finish()
    }

    private fun goToChangePatternActivity() {
        startActivity(Intent(this, SettingsPatternActivity::class.java))
        finish()
    }

    private fun goToLoginPatternActivity() {
        val intent = Intent(this, LoginPatternActivity::class.java)
        intent.putExtra("TAG_SETTING", TAG_SETTING)
        startActivity(intent)
        finish()
    }

    private fun goToLanguageActivity() {
        startActivityForResult(
            Intent(this, SettingsLanguageActivity::class.java),
            SETTINGS_REQUEST_CODE
        )
    }

    private fun goToServerAcitivity() {
        val intent = Intent(this, ServerActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToAboutActivity() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    private fun goToLogActivity() {
        startActivity(Intent(this, LogActivity::class.java))
    }

    private fun goToLogout() {
        LocaleHelper.setLocale(this, BaseParam.APP_DEFAULT_LANG)
        viewModel.deleteUserSession()
    }

    private fun setDialogSwitchPattern() {
        currentTag = TAG_SWITCH_PATTERN
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.change_switch_dialog)
            .setDescription(R.string.change_switch_dialog_question)
            .setListener(this)
        customDialogUtils.show()
    }

    private fun setDialogLogout() {
        currentTag = TAG_LOGOUT
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.logout_title)
            .setDescription(R.string.logout_qustion)
            .setListener(this)
        customDialogUtils.show()
    }

    private fun setDialogChangePattern() {
        viewModel.validatePattern()
        viewModel.pattern.observe(this, Observer {
            if (it == null) {
                currentTag = TAG_ACTIVE_PATTERN
                customDialogUtils.setLeftButtonText(R.string.dialog_no)
                    .setRightButtonText(R.string.dialog_yes)
                    .setTittle(R.string.change_switch_dialog)
                    .setDescription(R.string.change_switch_dialog_question)
                    .setListener(this)
                customDialogUtils.show()
            } else {
                currentTag = TAG_CHANGE_PATTERN
                customDialogUtils.setLeftButtonText(R.string.dialog_no)
                    .setRightButtonText(R.string.dialog_yes)
                    .setTittle(R.string.change_pattern_dialog)
                    .setDescription(R.string.change_pattern_dialog_question)
                    .setListener(this)
                customDialogUtils.show()
            }
        })

    }

    override fun onRightButton() {
        when (currentTag) {
            TAG_ACTIVE_PATTERN -> goToLoginPatternActivity()
            TAG_CHANGE_PATTERN -> goToChangePatternActivity()
            TAG_SWITCH_PATTERN -> goToLoginPatternActivity()
            TAG_LOGOUT -> goToLogout()
        }
    }

    override fun onLeftButton() {
        when (currentTag) {
            TAG_SWITCH_PATTERN -> {
                customDialogUtils.dismiss()
                binding.activatePattern.isChecked = false
            }
            TAG_CHANGE_PATTERN -> customDialogUtils.dismiss()
            TAG_ACTIVE_PATTERN -> customDialogUtils.dismiss()
            TAG_LOGOUT -> customDialogUtils.dismiss()
        }
    }

    override fun onMiddleButton() {
        customDialogUtils.dismiss()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backPressToolbar()
    }

    override fun backPressToolbar() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}