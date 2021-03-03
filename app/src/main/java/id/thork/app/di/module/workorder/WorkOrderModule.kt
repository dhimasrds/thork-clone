package id.thork.app.di.module.workorder

import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.base.BaseParam
import id.thork.app.di.module.PreferenceManager
import id.thork.app.di.module.login.LoginModule
import id.thork.app.network.api.WorkOrderApi
import id.thork.app.network.api.WorkOrderClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */

@Module
@InstallIn(ActivityRetainedComponent::class)
object WorkOrderModule {
    val TAG = LoginModule::class.java.name

//    @Provides
//    @ActivityRetainedScoped
//    fun provideWorkOrderApi(retrofit: Retrofit, okHttpClient: OkHttpClient, preferenceManager: PreferenceManager): WorkOrderApi {
//        Timber.tag(TAG).i("provideLoginApi() init")
//        var serverUrl = preferenceManager.getString(BaseParam.APP_SERVER_ADDRESS)
//        if (serverUrl.isNullOrEmpty()) {
//            serverUrl = BaseParam.BASE_SERVER_URL
//        }
//        Timber.tag(TAG).i("provideLoginApi() serverUrl: %s", serverUrl)
//        retrofit.newBuilder().baseUrl(serverUrl)
//            .addConverterFactory(MoshiConverterFactory.create())
//            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
//            .client(okHttpClient)
//            .build()
//        return retrofit.create(WorkOrderApi::class.java)
//    }

    @Provides
    @ActivityRetainedScoped
    fun provideWorkOrderClient(workOrderApi: WorkOrderApi): WorkOrderClient {
        return WorkOrderClient(workOrderApi)
    }
}