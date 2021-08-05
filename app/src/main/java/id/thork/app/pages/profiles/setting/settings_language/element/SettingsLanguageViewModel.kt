package id.thork.app.pages.profiles.setting.settings_language.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.repository.LoginRepository

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class SettingsLanguageViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {

    fun setUserLanguage(selectedLang: String) {
        val userExisting: UserEntity? = loginRepository.findUserByPersonUID(appSession.personUID)
        userExisting!!.language = selectedLang
        loginRepository.saveLoginPattern(userExisting, appSession.userEntity.username)
    }
}