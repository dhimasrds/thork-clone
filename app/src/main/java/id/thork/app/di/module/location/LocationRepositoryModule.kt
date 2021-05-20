package id.thork.app.di.module.location

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.persistence.dao.LocationDaoImp
import id.thork.app.repository.LocationRepository

/**
 * Created by M.Reza Sulaiman on 19/05/2021
 * Jakarta, Indonesia.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object LocationRepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideLocationRepository(
    ): LocationRepository {
        return LocationRepository(LocationDaoImp())
    }
}