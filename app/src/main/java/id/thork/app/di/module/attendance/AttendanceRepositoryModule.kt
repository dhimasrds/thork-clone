package id.thork.app.di.module.attendance

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.dao.AttendanceDaoImp
import id.thork.app.repository.AttendanceRepository
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by M.Reza Sulaiman on 16/06/2021
 * Jakarta, Indonesia.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object AttendanceRepositoryModule {
    val TAG = AttendanceModule::class.java.name

    @Provides
    @ActivityRetainedScoped
    fun provideAttendanceRepository(
        context: Context,
        preferenceManager: PreferenceManager,
        appSession: AppSession,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): AttendanceRepository {
        return AttendanceRepository(
            context,
            preferenceManager,
            appSession,
            AttendanceDaoImp(),
            httpLoggingInterceptor
        )
    }

}