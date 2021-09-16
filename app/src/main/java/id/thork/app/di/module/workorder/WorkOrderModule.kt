package id.thork.app.di.module.workorder

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.RetrofitBuilder
import id.thork.app.network.api.*
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
    fun provideTaskApi(preferenceManager: PreferenceManager, httpLoggingInterceptor: HttpLoggingInterceptor): TaskApi {
        Timber.tag(TAG).i("provideLoginApi() init")
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(TaskApi::class.java)
    }


    @Provides
    @ActivityRetainedScoped
    fun provideStoreroomApi(preferenceManager: PreferenceManager, httpLoggingInterceptor: HttpLoggingInterceptor): StoreroomApi {
        Timber.tag(TAG).i("provideStoreroomApi() init")
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(StoreroomApi::class.java)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideMaterialApi(preferenceManager: PreferenceManager, httpLoggingInterceptor: HttpLoggingInterceptor): MaterialApi {
        Timber.tag(TAG).i("provideMaterialApi() init")
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(MaterialApi::class.java)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideWorkOrderClient(workOrderApi: WorkOrderApi): WorkOrderClient {
        return WorkOrderClient(workOrderApi)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideStoreroomClient(storeroomApi: StoreroomApi): StoreroomClient {
        return StoreroomClient(storeroomApi)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideMaterialClient(materialApi: MaterialApi): MaterialClient {
        return MaterialClient(materialApi)
    }
}