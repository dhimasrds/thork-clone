package id.thork.app.di.module.asset

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.persistence.dao.AssetDaoImp
import id.thork.app.persistence.dao.LocationDaoImp
import id.thork.app.repository.AssetRepository

/**
 * Created by M.Reza Sulaiman on 18/05/2021
 * Jakarta, Indonesia.
 */

@Module
@InstallIn(ActivityRetainedComponent::class)
object AssetRepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideAssetRepository(
    ): AssetRepository {
        return AssetRepository(AssetDaoImp(),LocationDaoImp())
    }
}