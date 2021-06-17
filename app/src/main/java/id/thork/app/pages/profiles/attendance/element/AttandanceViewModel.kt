package id.thork.app.pages.profiles.attendance.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.persistence.entity.AttendanceEntity
import id.thork.app.repository.AttendanceRepository


/**
 * Created by Raka Putra on 6/11/21
 * Jakarta, Indonesia.
 */
class AttandanceViewModel @ViewModelInject constructor(
    private val appSession: AppSession,
    private val attendanceRepository: AttendanceRepository,
) : LiveCoroutinesViewModel() {

    private val _isCheckIn = MutableLiveData<AttachmentEntity>()
    val isCheckIn: LiveData<AttachmentEntity> get() = _isCheckIn
    private lateinit var checkIn: MutableList<AttendanceEntity>

    fun findCheckInAttendance(): AttendanceEntity? {
        return attendanceRepository.findCheckInAttendance()
    }


}