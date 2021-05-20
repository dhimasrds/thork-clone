package id.thork.app.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.persistence.dao.*
import id.thork.app.repository.MultiAssetRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
object MultiAssetRepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideMultiAssetRepository(
        preferenceManager: PreferenceManager
    ): MultiAssetRepository {
        return MultiAssetRepository(MultiAssetDaoImp(),preferenceManager)
    }
}