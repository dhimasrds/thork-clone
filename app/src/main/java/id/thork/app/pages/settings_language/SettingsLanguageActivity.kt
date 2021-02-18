package id.thork.app.pages.settings_language

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivitySettingsLanguageBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.settings_language.element.*
import id.thork.app.persistence.entity.Language
import id.thork.app.persistence.entity.LanguageEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class SettingsLanguageActivity : BaseActivity(), CustomDialogUtils.DialogActionListener {
    private val binding: ActivitySettingsLanguageBinding by binding(R.layout.activity_settings_language)
    private val settingsLanguageViewModel: SettingsLanguageViewModel by viewModels()

    private val english = "is selected!"
    private val bahasa = "Bahasa dipilih!"
    private lateinit var languageAdapter: SettingsLanguageAdapter
    private lateinit var customDialogUtils: CustomDialogUtils
    private var selectedLangCode: String? = null
    private var selectedLanguage: String? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@SettingsLanguageActivity
            settingsLanguage = settingsLanguageViewModel
        }
        customDialogUtils = CustomDialogUtils(this)
    }

    override fun setupListener() {
        super.setupListener()
        languageAdapter = SettingsLanguageAdapter(object : RecyclerViewItemClickListener {
            override fun onItemClicked(lang: LanguageEntity) {
                selectedLanguage = lang.language.toString()
                selectedLangCode = lang.languageCode.toString()
                showFailedReinputDialog()
            }
        })
    }

    override fun setupObserver() {
        super.setupObserver()
        binding.recyclerviewSettingsLanguage.adapter = languageAdapter
        binding.recyclerviewSettingsLanguage.layoutManager = LinearLayoutManager(this)
        binding.recyclerviewSettingsLanguage.addItemDecoration(DividerItemDecoration(this))
        binding.recyclerviewSettingsLanguage.apply {
            adapter = languageAdapter.withLoadStateFooter(DefaultLoadStateAdapter())
        }
        lifecycleScope.launch {
            settingsLanguageViewModel.languageList.collect {
                languageAdapter.submitData(it)

            }
        }
    }

    private fun showFailedReinputDialog() {
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
        finish()
    }

    override fun onLeftButton() {
        customDialogUtils.dismiss()
    }

    override fun onMiddleButton() {
        customDialogUtils.dismiss()
    }
}