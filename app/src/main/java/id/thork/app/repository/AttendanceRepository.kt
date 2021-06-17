package id.thork.app.repository

import android.content.Context
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
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
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
) : BaseRepository {

    var laborCode = appSession.userEntity.laborcode
    var username = appSession.userEntity.username
    private var attendanceEntity: AttendanceEntity? = null


    fun saveAttendanceCache(attendanceEntity: AttendanceEntity) {
        return attendanceDao.createAttendanceCache(attendanceEntity, username.toString())
    }

    fun removeAttendance() {
        return attendanceDao.remove()
    }

    fun findCheckInAttendance(): AttendanceEntity? {
        return attendanceDao.findCheckInAttendance()
    }

    fun saveCache(
        isCheckin: Boolean, dateLocal: Long, date: String, hours: String,
        longitudex: String, latitudey: String, uriImage: String, dateTimeHeader: String,
        workHours: String?,
    ) {
        if (isCheckin) {
            val checkinEntity = AttendanceEntity(
                dateCheckInLocal = dateLocal,
                dateCheckIn = date,
                hoursCheckIn = hours,
                longCheckIn = longitudex,
                latCheckIn = latitudey,
                uriImageCheckIn = uriImage,
                dateTimeHeader = dateTimeHeader,
                username = username
            )
            saveAttendanceCache(checkinEntity)
        } else {
            attendanceEntity = findCheckInAttendance()
            attendanceEntity.whatIfNotNull {
                it.dateCheckOutLocal = dateLocal
                it.dateCheckOut = date
                it.hoursCheckOut = hours
                it.longCheckOut = longitudex
                it.latCheckOut = latitudey
                it.uriImageCheckOut = uriImage

                it.workHours = workHours
                it.syncUpdate = BaseParam.APP_FALSE
                it.offlineMode = BaseParam.APP_FALSE
                saveAttendanceCache(it)
            }
        }
    }
}