package id.thork.app.di.module.login

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.network.api.LoginClient
import id.thork.app.persistence.dao.SysPropDaoImp
import id.thork.app.persistence.dao.UserDaoImp
import id.thork.app.repository.LoginRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
object LoginRepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideLoginRepository(
        loginClient: LoginClient,
    ): LoginRepository {
        return LoginRepository(loginClient, UserDaoImp(), SysPropDaoImp())
    }
}