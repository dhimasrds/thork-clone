package id.thork.app.pages.settings.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.repository.AttachmentRepository
import id.thork.app.repository.LoginRepository
import id.thork.app.repository.MaterialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class SettingsViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val appSession: AppSession,
    private val preferenceManager: PreferenceManager,
    private val attachmentRepository: AttachmentRepository,
    private val materialRepository: MaterialRepository
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
        userExisting.whatIfNotNull {
            it.isPattern = activate
            loginRepository.saveLoginPattern(it, appSession.userEntity.username)
        }
    }

    fun logout() {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        viewModelScope.launch(Dispatchers.IO) {
            appSession.userHash.whatIfNotNull {
                loginRepository.logout(cookie, it,
                    onSuccess = {
                        deleteUserSession()
                        Timber.tag(TAG).i("logoutCookie() sessionTime: %s", it)
                    }, onError = {
                        Timber.tag(TAG).i("logoutCookie() error: %s", it)
                    })
            }
        }
    }

    private fun deleteUserSession() {
        val userEntity: UserEntity = appSession.userEntity
        loginRepository.deleteUserSession(userEntity)
        //TODO delete system properties
        loginRepository.deleteAssetEntity()
        loginRepository.deleteSystemProperties()
        loginRepository.deleteSystemResource()
        loginRepository.deleteWoPropertios()
        loginRepository.deleteMultiAssetEntity()
        attachmentRepository.deleteAttachmentCache()
        materialRepository.removeItemMaster()
        materialRepository.removeMaterialPlan()
        materialRepository.removeListMaterialActual()
        _logout.postValue(BaseParam.APP_TRUE)
    }


}