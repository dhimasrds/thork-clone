package id.thork.app.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.thork.app.BuildConfig
import id.thork.app.network.HttpRequestInterceptor
import okhttp3.Cache
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val TAG = NetworkModule::class.java.name

    @Singleton
    @Provides
    fun provideHttpCache(application: Application): Cache {
        val cacheSize:Long = 10 * 1024 * 1024
        val cache = Cache(application.cacheDir, cacheSize)
        return cache
    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger{
            override fun log(message: String) {
                Timber.d("Interceptor Log: $message")
            }
        })

        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return httpLoggingInterceptor
    }

    @Singleton
    @Provides
    fun provideHttpRequestInterceptor(): HttpRequestInterceptor {
        return HttpRequestInterceptor()
    }
}