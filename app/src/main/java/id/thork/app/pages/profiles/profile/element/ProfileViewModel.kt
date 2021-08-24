package id.thork.app.pages.profiles.profile.element

import android.widget.ImageView
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppResourceMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.ApiParam
import id.thork.app.network.GlideApp
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
    private val attendanceRepository: AttendanceRepository,
    private val taskRepository: TaskRepository,
    private val appResourceMx: AppResourceMx,
    private val laborRepository: LaborRepository
) : LiveCoroutinesViewModel() {
    val TAG = ProfileViewModel::class.java.name

    private val _logout = MutableLiveData<Int>()
    private val _username = MutableLiveData<String>()
    private val _profileRole = MutableLiveData<String>()
    private val _profilePicture = MutableLiveData<String>()

    val logout: LiveData<Int> get() = _logout
    val username: LiveData<String> get() = _username
    val profileRole: LiveData<String> get() = _profileRole
    val profilePicture: LiveData<String> get() = _profilePicture

    fun validateUsername() {
        _username.value = appSession.userEntity.laborcode
        _profileRole.value = appSession.userEntity.jobcodeDescription
        _profilePicture.value = appSession.userEntity.imageLibRef
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

    fun fetchAttendance() {
        //TODO appSession.cookie still null
        val cookie1 = appSession.cookie
        Timber.tag(TAG).i("fetchAttendance() cookie: %s", cookie1)
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val savedQuery = appResourceMx.fsmResAttendace
        val select = ApiParam.API_SELECT_ALL
        viewModelScope.launch(Dispatchers.IO) {
            cookie.whatIfNotNull { cookie ->
                savedQuery.whatIfNotNull { savedQuery ->
                    attendanceRepository.fetchAttendance(cookie, savedQuery, select,
                        onSuccess = {
                            it.whatIfNotNull { response ->
                                response.member.whatIfNotNullOrEmpty { list ->
                                    list[0].whatIfNotNull { member ->
                                        Timber.tag(TAG).i("fetchAttendance() member: %s", member)
                                        attendanceRepository.handlingFetchAttendance(member)
                                    }
                                }
                            }
                        },
                        onError = {
                            Timber.tag(TAG).i("fetchAttendance() error: %s", it)
                        })
                }
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
        taskRepository.removeAllTask()
        laborRepository.removeCacheLabor()
        _logout.postValue(BaseParam.APP_TRUE)
    }

    fun setImageProfile(
        imageUri: String,
        imageView: ImageView
    ) {
        imageUri.whatIfNotNull {
            if (it.startsWith("https")) {
                val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
                Timber.tag(TAG).d("setImageProfile() cookies: %s", cookie)
                val glideUrl = GlideUrl(
                    it, LazyHeaders.Builder()
                        .addHeader("Cookie", cookie)
                        .build()
                )
                GlideApp.with(BaseApplication.context).load(glideUrl)
                    .circleCrop()
                    .into(imageView)
            }
        }
    }
}