package id.thork.app.pages.profiles.profile.element

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
import id.thork.app.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Raka Putra on 6/11/21
 * Jakarta, Indonesia.
 */
class ProfileViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val appSession: AppSession,
    private val preferenceManager: PreferenceManager,
    private val attachmentRepository: AttachmentRepository,
    private val materialRepository: MaterialRepository,
    private val worklogRepository: WorklogRepository,
    private val attendanceRepository: AttendanceRepository
) : LiveCoroutinesViewModel() {
    val TAG = ProfileViewModel::class.java.name

    private val _logout = MutableLiveData<Int>()
    private val _username = MutableLiveData<String>()

    val logout: LiveData<Int> get() = _logout
    val username: LiveData<String> get() = _username

    fun validateUsername() {
        _username.value = appSession.userEntity.laborcode
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
        worklogRepository.removeWorklogType()
        worklogRepository.removeWorklog()
        attendanceRepository.removeAttendance()
        _logout.postValue(BaseParam.APP_TRUE)
    }
}