package id.thork.app.pages.settings.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.repository.LoginRepository

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class SettingsViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {

    val selectedLang: LiveData<String> get() = _selectedLang
    private val _selectedLang = MutableLiveData<String>()

    fun validateLanguage(){
        _selectedLang.value = appSession.userEntity.language
    }
}