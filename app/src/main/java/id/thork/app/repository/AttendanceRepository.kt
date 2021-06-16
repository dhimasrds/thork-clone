package id.thork.app.repository

import android.content.Context
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.dao.AttendanceDao
import id.thork.app.persistence.entity.AttendanceEntity
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

/**
 * Created by M.Reza Sulaiman on 16/06/2021
 * Jakarta, Indonesia.
 */
class AttendanceRepository @Inject constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val appSession: AppSession,
    private val attendanceDao: AttendanceDao,
    private val httpLoggingInterceptor: HttpLoggingInterceptor
) : BaseRepository {

    var laborCode = appSession.userEntity.laborcode
    var username = appSession.userEntity.username

    fun saveAttendanceCache(attendanceEntity: AttendanceEntity) {
        return attendanceDao.createAttendanceCache(attendanceEntity, username.toString())
    }

    fun removeAttendance() {
        return attendanceDao.remove()
    }


}