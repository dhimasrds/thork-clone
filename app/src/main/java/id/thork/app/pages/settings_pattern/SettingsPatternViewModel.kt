package id.thork.app.pages.settings_pattern

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.repository.LoginRepository

/**
 * Created by M.Reza Sulaiman on 08/01/21
 * Jepara, Indonesia.
 */
class SettingsPatternViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {

    private val _username = MutableLiveData<String>()
    private val _isPattern =  MutableLiveData<Int>()
    private val _validatePattern = MutableLiveData<Int>()
    private val _switchUser = MutableLiveData<Int>()

    val username: LiveData<String> get() = _username
    val isPatttern: LiveData<Int> get() = _isPattern
    val validatePattern: LiveData<Int> get() = _validatePattern
    val switchUser: LiveData<Int> get() = _switchUser

    fun validateUsername() {
        _username.value = appSession.userEntity.username
        _isPattern.value = appSession.userEntity.isPattern
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

        if(pattern.equals(existingPattern)) {
            _validatePattern.value = BaseParam.APP_TRUE
        } else {
            _validatePattern.value = BaseParam.APP_FALSE
        }
    }

    fun deleteUserSession() {
        val userEntity: UserEntity = appSession.userEntity
        loginRepository.deleteUserSession(userEntity)
        _switchUser.value = BaseParam.APP_TRUE
    }



}