package id.thork.app.pages.profiles.setting.settings_language

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivitySettingsLanguageBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.profiles.setting.settings_language.element.LanguageAdapter
import id.thork.app.pages.profiles.setting.settings_language.element.LanguageAdapterItemClickListener
import id.thork.app.pages.profiles.setting.settings_language.element.SettingsLanguageViewModel
import id.thork.app.persistence.entity.Language
import java.util.*

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class SettingsLanguageActivity : BaseActivity(), CustomDialogUtils.DialogActionListener {
    private val binding: ActivitySettingsLanguageBinding by binding(R.layout.activity_settings_language)
    private val settingsLanguageViewModel: SettingsLanguageViewModel by viewModels()

    private lateinit var languageAdapter: LanguageAdapter
    private lateinit var customDialogUtils: CustomDialogUtils
    private var selectedLangCode: String? = null
    private var selectedLanguage: String? = null
    private val english = " is selected!"
    private val bahasa = "Bahasa dipilih!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initAdapter()
    }

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@SettingsLanguageActivity
            settingsLanguage = settingsLanguageViewModel
        }
        customDialogUtils = CustomDialogUtils(this)
//        setupToolbar()
        setupToolbarWithHomeNavigation(getString(R.string.change_language_dialog), navigation = false,
            filter = false, scannerIcon = false,
            notification = false, option = false,
            historyAttendanceIcon = false
        )

    }

    private fun initAdapter() {
        val listLanguage = listOf(
            Language(languageCode = BaseParam.APP_DEFAULT_LANG ,language = BaseParam.APP_DEFAULT_LANG_DETAIL),
            Language(languageCode = BaseParam.APP_IND_LANG ,language = BaseParam.APP_IND_LANG_DETAIL),
        )

        languageAdapter = LanguageAdapter(object : LanguageAdapterItemClickListener {
            override fun onItemClicked(lang: Language) {
                selectedLanguage = lang.language.toString()
                selectedLangCode = lang.languageCode.toString()
                showChangeLanguageDialog()
            }
        }, listLanguage)
        binding.recyclerviewSettingsLanguage.adapter = languageAdapter
        binding.recyclerviewSettingsLanguage.layoutManager = LinearLayoutManager(this)
    }

    private fun showChangeLanguageDialog() {
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.change_language_dialog)
            .setDescription(R.string.change_language_question)
            .setListener(this)
        customDialogUtils.show()
    }

    override fun onRightButton() {
        settingsLanguageViewModel.setUserLanguage(selectedLanguage!!)
        customDialogUtils.dismiss()
        val intent = Intent()
        intent.putExtra(BaseParam.SELECTED_LANG_CODE, selectedLangCode)
        setResult(Activity.RESULT_OK, intent)
        createToast()
        finish()
    }

    override fun onLeftButton() {
        customDialogUtils.dismiss()
    }

    override fun onMiddleButton() {
        customDialogUtils.dismiss()
    }

    private fun createToast() {
        if (selectedLanguage == BaseParam.APP_DEFAULT_LANG_DETAIL){
            Toast.makeText(this, BaseParam.APP_DEFAULT_LANG_DETAIL + english, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, bahasa, Toast.LENGTH_SHORT).show()
        }
    }

    override fun goToPreviousActivity() {
        //finish()
        super.goToPreviousActivity()
    }
}