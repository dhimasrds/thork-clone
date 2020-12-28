package id.thork.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.repository.LoginRepository
import timber.log.Timber

class MainViewModel @AssistedInject constructor(
    private val loginRepository: LoginRepository,
    private val appSession: AppSession,
    @Assisted private val name: String
) : LiveCoroutinesViewModel() {
    val TAG = MainViewModel::class.java.name

    init {
        Timber.tag(TAG).i("init() appSession: %s username: %s", appSession, appSession.userEntity.username)
    }

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(name: String): MainViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            name: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(name) as T
            }
        }
    }
}