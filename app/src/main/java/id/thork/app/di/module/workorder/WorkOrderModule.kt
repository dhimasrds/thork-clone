package id.thork.app.di.module.workorder

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.RetrofitBuilder
import id.thork.app.network.api.DoclinksApi
import id.thork.app.network.api.WorkOrderApi
import id.thork.app.network.api.WorkOrderClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */

@Module
@InstallIn(ActivityRetainedComponent::class)
object WorkOrderModule {
    val TAG = WorkOrderModule::class.java.name

    @Provides
    @ActivityRetainedScoped
    fun provideWorkOrderApi(preferenceManager: PreferenceManager, httpLoggingInterceptor: HttpLoggingInterceptor): WorkOrderApi {
        Timber.tag(TAG).i("provideLoginApi() init")
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(WorkOrderApi::class.java)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideDoclinksApi(preferenceManager: PreferenceManager, httpLoggingInterceptor: HttpLoggingInterceptor): DoclinksApi {
        Timber.tag(TAG).i("provideLoginApi() init")
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(DoclinksApi::class.java)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideWorkOrderClient(workOrderApi: WorkOrderApi): WorkOrderClient {
        return WorkOrderClient(workOrderApi)
    }


}