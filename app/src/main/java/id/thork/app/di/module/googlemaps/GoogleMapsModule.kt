package id.thork.app.di.module.googlemaps

import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.base.BaseParam
import id.thork.app.di.module.login.LoginModule
import id.thork.app.network.HttpRequestInterceptor
import id.thork.app.network.api.GoogleMapsApi
import id.thork.app.network.api.GoogleMapsClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Named

/**
 * Created by M.Reza Sulaiman on 18/02/21
 * Jakarta, Indonesia.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object GoogleMapsModule {
    val TAG = GoogleMapsModule::class.java.name

    @Named("googleApisClient")
    @Provides
    @ActivityRetainedScoped
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {

        val httpRequestInterceptor = HttpRequestInterceptor()
        httpRequestInterceptor.setHost(BaseParam.BASE_MAPS_URL)
        Timber.tag(LoginModule.TAG).i("provideOkHttpClient() init map address: %s", BaseParam.BASE_MAPS_URL)
        return OkHttpClient.Builder()
            .addInterceptor(httpRequestInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Named("googleApis")
    @Provides
    @ActivityRetainedScoped
    fun provideRetrofit(
        @Named("googleApisClient") okHttpClient: OkHttpClient
    ): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(BaseParam.BASE_MAPS_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
            .client(okHttpClient)
            .build()
        Timber.tag(TAG).i("provideRetrofit() init retrofit googleApisClient: %s", retrofit)
        return retrofit
    }

    @Provides
    @ActivityRetainedScoped
    fun provideGoogleMapsApi(@Named("googleApis") retrofit: Retrofit): GoogleMapsApi {
        Timber.tag(LoginModule.TAG).i("provideLoginApi() init retrofit: %s", retrofit)
        return retrofit.create(GoogleMapsApi::class.java)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideGoogleMapsClient(googleMapsApi: GoogleMapsApi): GoogleMapsClient {
        return GoogleMapsClient(googleMapsApi)
    }

}