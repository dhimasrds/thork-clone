package id.thork.app

import androidx.hilt.Assisted
import androidx.lifecycle.*
import com.squareup.inject.assisted.AssistedInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.network.model.user.UserResponse
import id.thork.app.repository.LoginRepository
import java.lang.reflect.Member

class MainViewModel @AssistedInject constructor(loginRepository: LoginRepository,
@Assisted private val name: String) : LiveCoroutinesViewModel() {
    val memberInfo: LiveData<UserResponse>

    init {
        memberInfo =  launchOnViewModelScope {
            loginRepository.loginByPerson(select=name, where = "",
                onSuccess = { print("success")},
                onError = { it }).asLiveData()
        }
    }

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(name: String): MainViewModel
    }

    companion object {
        fun provideFactory (
            assistedFactory: AssistedFactory,
            name: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @SuppressWarnings("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(name) as T
            }
        }
    }
}