package id.thork.app.di.module.attendance

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.RetrofitBuilder
import id.thork.app.network.api.AttendanceApi
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by M.Reza Sulaiman on 16/06/2021
 * Jakarta, Indonesia.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object AttendanceModule {
    val TAG = AttendanceRepositoryModule::class.java.name

    @Provides
    @ActivityRetainedScoped
    fun provideAttendanceApi(preferenceManager: PreferenceManager, httpLoggingInterceptor: HttpLoggingInterceptor): AttendanceApi {
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(AttendanceApi::class.java)
    }

}
