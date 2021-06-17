package id.thork.app.pages.profiles.attendance.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.persistence.entity.AttendanceEntity
import id.thork.app.repository.AttendanceRepository


/**
 * Created by Raka Putra on 6/11/21
 * Jakarta, Indonesia.
 */
class AttandanceViewModel @ViewModelInject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {

    fun findCheckInAttendance(): AttendanceEntity? {
        return attendanceRepository.findCheckInAttendance()
    }

    fun saveCache(
        isCheckin: Boolean, dateLocal: Long, date: String, hours: String,
        longitudex: String, latitudey: String, uriImage: String, dateTimeHeader: String,
        workHours: String?,
    ) {
        attendanceRepository.saveCache(isCheckin, dateLocal, date, hours,
            longitudex, latitudey, uriImage, dateTimeHeader, workHours)
    }

    private fun updateToMaximo() {

    }

    private fun updateCheckIn() {
        val attendanceCache = attendanceRepository

    }

    private fun updateCheckOut() {

    }
}