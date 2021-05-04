package id.thork.app.di.module.systemproperties

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.persistence.dao.SysPropDaoImp
import id.thork.app.repository.SystemPropertiesRepository

/**
 * Created by M.Reza Sulaiman on 29/04/21
 * Jakarta, Indonesia.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object SystemPropertiesRepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideSystemPropertiesRepository(): SystemPropertiesRepository {
        return SystemPropertiesRepository(SysPropDaoImp())
    }
}