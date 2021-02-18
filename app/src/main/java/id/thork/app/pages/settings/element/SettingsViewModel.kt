package id.thork.app.pages.settings.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.persistence.dao.LanguageDao
import id.thork.app.persistence.dao.LanguageDaoImp
import id.thork.app.persistence.entity.LanguageEntity
import id.thork.app.repository.LanguageRepository
import id.thork.app.repository.LoginRepository
import kotlinx.coroutines.launch

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class SettingsViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {

    private val dao: LanguageDao
    private val repository: LanguageRepository
    val selectedLang: LiveData<String> get() = _selectedLang
    private val _selectedLang = MutableLiveData<String>()

    init {
        dao = LanguageDaoImp()
        repository = LanguageRepository(dao)
    }

    fun validateLanguage(){
        _selectedLang.value = appSession.userEntity.language
    }

    fun save(languageEntity: LanguageEntity) {
        viewModelScope.launch {
            repository.createNote(languageEntity)
        }
    }
}