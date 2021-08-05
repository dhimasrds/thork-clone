package id.thork.app.pages.profiles.setting.settings.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.repository.AttachmentRepository
import id.thork.app.repository.LoginRepository
import id.thork.app.repository.MaterialRepository
import id.thork.app.repository.WorklogRepository

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class SettingsViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val appSession: AppSession,
    private val preferenceManager: PreferenceManager,
    private val attachmentRepository: AttachmentRepository,
    private val materialRepository: MaterialRepository,
    private val worklogRepository: WorklogRepository
) : LiveCoroutinesViewModel() {
    val TAG = SettingsViewModel::class.java.name

    val selectedLang: LiveData<String> get() = _selectedLang
    val pattern: LiveData<String> get() = _pattern
    val username: LiveData<String> get() = _username
    val isPattern: LiveData<Int> get() = _isPattern

    private val _selectedLang = MutableLiveData<String>()
    private val _pattern = MutableLiveData<String>()
    private val _username = MutableLiveData<String>()
    private val _isPattern = MutableLiveData<Int>()

    fun validateLanguage() {
        _selectedLang.value = appSession.userEntity.language
    }

    fun validatePattern() {
        _pattern.value = appSession.userEntity.pattern
    }

    fun validateUsername() {
        _username.value = appSession.userEntity.laborcode
    }

    fun validateLogin() {
        _isPattern.value = appSession.userEntity.isPattern
    }

    fun setUserIsPattern(activate: Int) {
        val userExisting: UserEntity? = loginRepository.findUserByPersonUID(appSession.personUID)
        userExisting.whatIfNotNull {
            it.isPattern = activate
            loginRepository.saveLoginPattern(it, appSession.userEntity.username)
        }
    }
}