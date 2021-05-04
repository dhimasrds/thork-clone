package id.thork.app.di.module.firebase

import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.base.BaseParam
import id.thork.app.network.HttpRequestInterceptor
import id.thork.app.network.api.FirebaseApi
import id.thork.app.network.api.FirebaseClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Named

/**
 * Created by M.Reza Sulaiman on 20/04/21
 * Jakarta, Indonesia.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object FirebaseModule {
    val TAG = FirebaseModule::class.java.name

    @Named("firebaseApisClient")
    @Provides
    @ActivityRetainedScoped
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {

        val httpRequestInterceptor = HttpRequestInterceptor()
        httpRequestInterceptor.setHost(BaseParam.BASE_FIREBASE_URL)
        Timber.tag(FirebaseModule.TAG).i("provideOkHttpClient() init firebase address: %s", BaseParam.BASE_FIREBASE_URL)
        return OkHttpClient.Builder()
            .addInterceptor(httpRequestInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Named("firebaseApis")
    @Provides
    @ActivityRetainedScoped
    fun provideRetrofit(
        @Named("firebaseApisClient") okHttpClient: OkHttpClient
    ): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(BaseParam.BASE_FIREBASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
            .client(okHttpClient)
            .build()
        Timber.tag(FirebaseModule.TAG).i("provideRetrofit() init retrofit firebaseApiClient: %s", retrofit)
        return retrofit
    }


    @Provides
    @ActivityRetainedScoped
    fun provideFirebaseApi(@Named("firebaseApis") retrofit: Retrofit): FirebaseApi {
        return retrofit.create(FirebaseApi::class.java)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideFirebaseClient(firebaseApi: FirebaseApi): FirebaseClient {
        return FirebaseClient(firebaseApi)
    }






}