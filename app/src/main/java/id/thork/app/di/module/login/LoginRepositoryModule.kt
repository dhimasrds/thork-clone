package id.thork.app.di.module.login

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.api.LoginClient
import id.thork.app.persistence.dao.*
import id.thork.app.repository.LoginRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
object LoginRepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideLoginRepository(
        loginClient: LoginClient,
        preferenceManager: PreferenceManager
    ): LoginRepository {
        return LoginRepository(loginClient, UserDaoImp(), preferenceManager, SysPropDaoImp(), SysResDaoImp(), AssetDaoImp(), WoCacheDaoImp(), MultiAssetDaoImp())
    }
}