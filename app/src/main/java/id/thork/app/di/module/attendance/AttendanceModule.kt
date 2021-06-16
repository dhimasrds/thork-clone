package id.thork.app.di.module.attendance

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

/**
 * Created by M.Reza Sulaiman on 16/06/2021
 * Jakarta, Indonesia.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object AttendanceModule {
    val TAG = AttendanceRepositoryModule::class.java.name

}
