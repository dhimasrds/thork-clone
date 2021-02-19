package id.thork.app.pages.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivitySettingsBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.DialogUtils
import id.thork.app.pages.about.AboutActivity
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
    private val viewModel: SettingsViewModel by viewModels()
    private val binding: ActivitySettingsBinding by binding(R.layout.activity_settings)
    private val SETTINGS_REQUEST_CODE = 0
    private val TAG = SettingsActivity::class.java.name

    private lateinit var customDialogUtils: CustomDialogUtils
    private val TAG_SWITCH_PATTERN = "TAG_SWITCH_PATTERN"
    private val TAG_CHANGE_PATTERN = "TAG_CHANGE_PATTERN"
    private val TAG_CLEAR_CACHE = "CLEAR_CACHE"
    private val TAG_LOGOUT = "TAG_LOGOUT"
    private val TAG_SETTING = "TAG_SETTING"
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
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.validateLanguage()
        viewModel.selectedLang.observe(this, Observer {
            binding.settingsLanguageTitle.text = it
        })
    }

    private fun handlerOnclick() {
        binding.settingsAbout.setOnClickListener {
            goToAboutActivity()
        }

        binding.settingsLogs.setOnClickListener {
//            goToLogActivity()
        }

        binding.settingsLanguage.setOnClickListener {
            goToLanguageActivity()
        }

        binding.settingsChangePattern.setOnClickListener {
            goToLoginPatternActivity()
        }

        binding.buttonLogout.setOnClickListener {

        }
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

    private fun goToLoginPatternActivity() {
        startActivity(Intent(this, SettingsPatternActivity::class.java))
    }

    private fun goToLanguageActivity() {
        startActivityForResult(Intent(this, SettingsLanguageActivity::class.java), SETTINGS_REQUEST_CODE)
    }

    private fun goToLogActivity() {
        startActivity(Intent(this, LogActivity::class.java))
    }

    private fun goToAboutActivity() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    private fun setDialogSwitchPattern() {
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.change_switch_dialog)
            .setDescription(R.string.change_switch_dialog_question)
            .setListener(this)
        customDialogUtils.show()
    }

    private fun setDialogChangePattern() {
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.change_pattern_dialog)
            .setDescription(R.string.change_pattern_dialog_question)
            .setListener(this)
        customDialogUtils.show()
    }

    private fun setDialogCache() {
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.settings_cache)
            .setDescription(R.string.settings_cache_detail)
            .setListener(this)
        customDialogUtils.show()
    }

    override fun onRightButton() {
        TODO("Not yet implemented")
    }

    override fun onLeftButton() {
        TODO("Not yet implemented")
    }

    override fun onMiddleButton() {
        TODO("Not yet implemented")
    }
}