package id.thork.app.di.module

import android.app.Application
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.thork.app.BuildConfig
import id.thork.app.base.BaseParam
import id.thork.app.di.module.login.LoginModule
import id.thork.app.network.HttpRequestInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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

    @Provides
    @Singleton
    fun provideHttpRequestInterceptor(): HttpRequestInterceptor {
        return HttpRequestInterceptor()
    }

//    @Provides
//    @Singleton
//    fun provideRetrofit(preferenceManager: PreferenceManager): Retrofit {
//        var serverAddress: String = preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS)
//        if (serverAddress.isNullOrEmpty()) {
//            serverAddress = "https://www.google.com"
//        }
//        val retrofit =  Retrofit.Builder()
//            .baseUrl(serverAddress)
//            .addConverterFactory(MoshiConverterFactory.create())
//            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
//            .client(OkHttpClient.Builder().build())
//            .build()
//        Timber.tag(TAG).i("provideRetrofit() init retrofit: %s", retrofit)
//        return retrofit
//    }
}