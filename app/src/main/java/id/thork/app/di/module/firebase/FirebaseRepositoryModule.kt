package id.thork.app.di.module.firebase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.network.api.FirebaseClient
import id.thork.app.repository.FirebaseRepository

/**
 * Created by M.Reza Sulaiman on 20/04/21
 * Jakarta, Indonesia.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object FirebaseRepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideFirebaseRepository ( firebaseClient: FirebaseClient) : FirebaseRepository {
        return FirebaseRepository(firebaseClient)
    }
}