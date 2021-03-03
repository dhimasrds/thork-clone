package id.thork.app.di.module

import android.app.Application
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.thork.app.BuildConfig
import id.thork.app.base.BaseParam
import id.thork.app.di.module.workorder.WorkOrderModule
import id.thork.app.network.HttpRequestInterceptor
import id.thork.app.network.api.WorkOrderApi
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

    @Singleton
    @Provides
    fun provideHttpRequestInterceptor(): HttpRequestInterceptor {
        return HttpRequestInterceptor()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        preferenceManager: PreferenceManager
    ): OkHttpClient {
        var serverAddress: String = preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS)
        if (serverAddress.isNullOrEmpty()) {
            serverAddress = "https://www.google.com"
        }
        Timber.tag(TAG).i("provideOkHttpClient() init server address: %s", serverAddress)
        val httpRequestInterceptor = HttpRequestInterceptor()
        httpRequestInterceptor.setHost(serverAddress)
        return OkHttpClient.Builder()
            .addInterceptor(httpRequestInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideWorkOrderApi(
        okHttpClient: OkHttpClient,
        preferenceManager: PreferenceManager
    ): WorkOrderApi {
        Timber.tag(WorkOrderModule.TAG).i("provideLoginApi() init")
        var serverUrl = preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS)
        if (serverUrl.isNullOrEmpty()) {
            serverUrl = BaseParam.BASE_SERVER_URL
        }
        Timber.tag(WorkOrderModule.TAG).i("provideLoginApi() serverUrl: %s", serverUrl)
        val retrofit = Retrofit.Builder().baseUrl(serverUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
            .client(okHttpClient)
            .build()
        return retrofit.create(WorkOrderApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(preferenceManager: PreferenceManager, okHttpClient: OkHttpClient): Retrofit {
        var serverAddress: String = preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS)
        if (serverAddress.isNullOrEmpty()) {
            serverAddress = "https://www.google.com"
        }
        var retrofit =  Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
            .client(okHttpClient)
            .build()
        Timber.tag(TAG).i("provideRetrofit() init retrofit: %s", retrofit)
        return retrofit
    }

}