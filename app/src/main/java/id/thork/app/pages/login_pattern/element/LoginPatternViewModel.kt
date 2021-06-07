package id.thork.app.pages.login_pattern.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppPropertiesMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.repository.AttachmentRepository
import id.thork.app.repository.LoginRepository
import id.thork.app.repository.MaterialRepository
import id.thork.app.repository.WorklogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by M.Reza Sulaiman on 08/01/21
 * Jepara, Indonesia.
 */
class LoginPatternViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val appSession: AppSession,
    private val preferenceManager: PreferenceManager,
    private val appPropertiesMx: AppPropertiesMx,
    private val attachmentRepository: AttachmentRepository,
    private val materialRepository: MaterialRepository,
    private val worklogRepository: WorklogRepository
) : LiveCoroutinesViewModel() {
    val TAG = LoginPatternViewModel::class.java.name

    private val _username = MutableLiveData<String>()
    private val _isPattern = MutableLiveData<Int>()
    private val _validatePattern = MutableLiveData<Int>()
    private val _switchUser = MutableLiveData<Int>()
    private val _isSwitchUser = MutableLiveData<String>()

    val username: LiveData<String> get() = _username
    val isPatttern: LiveData<Int> get() = _isPattern
    val validatePattern: LiveData<Int> get() = _validatePattern
    val switchUser: LiveData<Int> get() = _switchUser
    val isSwitchUser: LiveData<String> get() = _isSwitchUser

    fun validateUsername() {
        _username.value = appSession.userEntity.username
        _isPattern.value = appSession.userEntity.isPattern
        _isSwitchUser.value = appPropertiesMx.fsmEnableSwitchUser
    }

    fun setUserPattern(pattern: String) {
        val userExisting: UserEntity? = loginRepository.findUserByPersonUID(appSession.personUID)
        userExisting!!.isPattern = BaseParam.APP_TRUE
        userExisting.pattern = pattern
        loginRepository.saveLoginPattern(userExisting, appSession.userEntity.username)
        _validatePattern.value = BaseParam.APP_TRUE
    }

    fun validatePattern(pattern: String) {
        val userExisting: UserEntity? = loginRepository.findUserByPersonUID(appSession.personUID)
        val existingPattern = userExisting!!.pattern

        if (pattern.equals(existingPattern)) {
            _validatePattern.value = BaseParam.APP_TRUE
        } else {
            _validatePattern.value = BaseParam.APP_FALSE
        }
    }

    fun switchUser() {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.logout(cookie, appSession.userHash!!,
                onSuccess = {
                    deleteUserSession()
                    Timber.tag(TAG).i("logoutCookie() sessionTime: %s", it)
                }, onError = {
                    Timber.tag(TAG).i("logoutCookie() error: %s", it)
                })
        }
    }

    fun deleteUserSession() {
        val userEntity: UserEntity = appSession.userEntity
        loginRepository.deleteUserSession(userEntity)
        loginRepository.deleteSystemProperties()
        loginRepository.deleteSystemResource()
        loginRepository.deleteWoPropertios()
        loginRepository.deleteAssetEntity()
        loginRepository.deleteMultiAssetEntity()
        attachmentRepository.deleteAttachmentCache()
        materialRepository.removeItemMaster()
        materialRepository.removeMaterialPlan()
        materialRepository.removeListMaterialActual()
        worklogRepository.removeWorklogType()
        worklogRepository.removeWorklog()
        _switchUser.postValue(BaseParam.APP_TRUE)
    }


}