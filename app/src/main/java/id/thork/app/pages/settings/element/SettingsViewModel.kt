package id.thork.app.pages.settings.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.pages.login.element.LoginViewModel
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class SettingsViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {
    val TAG = SettingsViewModel::class.java.name

    val selectedLang: LiveData<String> get() = _selectedLang
    val pattern: LiveData<String> get() = _pattern
    val username: LiveData<String> get() = _username
    val isPattern: LiveData<Int> get() = _isPattern
    val logout: LiveData<Int> get() = _logout


    private val _selectedLang = MutableLiveData<String>()
    private val _pattern = MutableLiveData<String>()
    private val _username = MutableLiveData<String>()
    private val _isPattern = MutableLiveData<Int>()
    private val _logout = MutableLiveData<Int>()


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
        userExisting!!.isPattern = activate
        loginRepository.saveLoginPattern(userExisting, appSession.userEntity.username)
    }

    fun deleteUserSession() {
        val userEntity: UserEntity = appSession.userEntity
        loginRepository.deleteUserSession(userEntity)
        _logout.postValue(BaseParam.APP_TRUE)
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.loginCookie(appSession.userHash!!,
                onSuccess = {
                    deleteUserSession()
                    Timber.tag(TAG).i("loginCookie() sessionTime: %s", it.sessiontimeout.toString())
                }, onError = {
                    Timber.tag(TAG).i("loginCookie() error: %s", it)
                })
        }
    }


}