package id.thork.app.di.module.googlemaps

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.network.api.GoogleMapsClient
import id.thork.app.repository.GoogleMapsRepository

/**
 * Created by M.Reza Sulaiman on 18/02/21
 * Jakarta, Indonesia.
 */

@Module
@InstallIn(ActivityRetainedComponent::class)
object GoogleMapsRepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideGoogleMapsRepository ( googleMapsClient: GoogleMapsClient) : GoogleMapsRepository{
        return GoogleMapsRepository(googleMapsClient)
    }
}