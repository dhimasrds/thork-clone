package id.thork.app.pages.settings_language.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.persistence.dao.LanguageDao
import id.thork.app.persistence.dao.LanguageDaoImp
import id.thork.app.persistence.entity.LanguageEntity
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.repository.LanguageRepository
import id.thork.app.repository.LoginRepository
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class SettingsLanguageViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {
    private val dao: LanguageDao
    private val repository: LanguageRepository
    val getAllLanguage: List<LanguageEntity>

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }

    init {
        dao = LanguageDaoImp()
        repository = LanguageRepository(dao)
        getAllLanguage = repository.getAllLanguage()
    }

    fun save(languageEntity: LanguageEntity) {
        viewModelScope.launch {
            repository.createNote(languageEntity)
        }
    }

    fun setUserLanguage(selectedLang: String) {
        val userExisting: UserEntity? = loginRepository.findUserByPersonUID(appSession.personUID)
        userExisting!!.language = selectedLang
        loginRepository.saveLoginPattern(userExisting, appSession.userEntity.username)
        Timber.d("raka %s", selectedLang)
    }

    val languageList = Pager(
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = true
        ),
        pagingSourceFactory = { SettingsLanguagePagingSource(dao, repository) }
    ).flow.cachedIn(viewModelScope)
}