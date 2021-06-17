package id.thork.app.repository

import android.content.Context
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.dao.AttendanceDao
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by M.Reza Sulaiman on 17/06/2021
 * Jakarta, Indonesia.
 */
class WorkerAttendanceRepository constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
    private val appSession: AppSession,
    private val attendanceDao: AttendanceDao,
) {

    fun buildAttendanceRepository(): AttendanceRepository {
        return AttendanceRepository(
            context, preferenceManager, appSession, attendanceDao, httpLoggingInterceptor
        )
    }
}