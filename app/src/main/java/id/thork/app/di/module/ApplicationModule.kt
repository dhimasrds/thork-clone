package id.thork.app.di.module

import android.app.Application
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.thork.app.di.ApiKey
import id.thork.app.di.LibraryKey
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideContext(application: Application) = application.baseContext
    
    @Singleton
    @Provides
    fun provideMoshi() = Moshi.Builder().build()

    @ApiKey
    @Provides
    fun provideBaseUrl() = "Hello Dagger Hilt"

    @LibraryKey
    @Provides
    fun provideLibBaseUrl() = "Hello Dagger Hilt Lib"

}
