package id.thork.app.pages.profiles.attendance.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.persistence.entity.AttendanceEntity
import id.thork.app.repository.AttendanceRepository
import timber.log.Timber


/**
 * Created by Raka Putra on 6/11/21
 * Jakarta, Indonesia.
 */
class AttandanceViewModel @ViewModelInject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {
    private lateinit var attendanceEntities: List<AttendanceEntity>
    private val _attendance = MutableLiveData<List<AttendanceEntity>>()
    val attendance: LiveData<List<AttendanceEntity>> get() = _attendance

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

    fun fetchAttendance() {
        attendanceEntities = attendanceRepository.findlistAttendanceCacheLocal()
        _attendance.value = attendanceEntities
    }

    private fun updateToMaximo() {

    }

    private fun updateCheckIn() {
        val attendanceCache = attendanceRepository

    }

    private fun updateCheckOut() {

    }
}