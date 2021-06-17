package id.thork.app.pages.profiles.attendance.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.response.attendance_response.Member
import id.thork.app.persistence.entity.AttendanceEntity
import id.thork.app.repository.AttendanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * Created by Raka Putra on 6/11/21
 * Jakarta, Indonesia.
 */
class AttandanceViewModel @ViewModelInject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val appSession: AppSession,
    private val preferenceManager: PreferenceManager,
) : LiveCoroutinesViewModel() {
    val TAG = AttandanceViewModel::class.java.name


    fun findCheckInAttendance(): AttendanceEntity? {
        return attendanceRepository.findCheckInAttendance()
    }

    fun saveCache(
        isCheckin: Boolean, dateLocal: Long, date: String, hours: String,
        longitudex: String, latitudey: String, uriImage: String, dateTimeHeader: String,
        workHours: String?, attendanceId: Int
    ) {
        attendanceRepository.saveCache(
            isCheckin, dateLocal, date, hours,
            longitudex, latitudey, uriImage, dateTimeHeader, workHours
        )
        updateToMaximo(isCheckin, attendanceId)
    }

    private fun updateToMaximo(isCheckin: Boolean, attendanceId: Int) {
        if (isCheckin) {
            updateCheckIn()
        } else {
            updateCheckOut(attendanceId)
        }
    }

    private fun updateCheckIn() {

        val localAttendance = attendanceRepository.prepareBodyCheckIn()
        var member = Member()
        localAttendance.whatIfNotNull {
            member = it
        }

        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val contentType: String = ("application/json")
        val properties: String = BaseParam.APP_ALL_PROPERTIES
        viewModelScope.launch(Dispatchers.IO) {
            attendanceRepository.createAttendanceToMx(
                cookie,
                contentType,
                properties,
                member,
                onSuccess = {
                    it.attendanceid.whatIfNotNull { attendanceid ->
                        attendanceRepository.handlingCheckInAfterUpdate(attendanceid)
                    }

                },
                onError = {
                    Timber.tag(TAG).i("updateCheckIn() onError() onError: %s", it)
                }
            )
        }

    }

    private fun updateCheckOut(attendanceId: Int) {
        val localAttendance = attendanceRepository.prepareBodyCheckOut(attendanceId)
        var member = Member()
        localAttendance.whatIfNotNull {
            member = it
        }

        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType: String = ("application/json")
        val patchType: String = BaseParam.APP_MERGE
        viewModelScope.launch(Dispatchers.IO) {
            attendanceRepository.updateAttendanceToMx(
                cookie,
                xMethodeOverride,
                contentType,
                patchType,
                attendanceId,
                member,
                onSuccess = {
                    attendanceRepository.handlingCheckOutAfterUpdate(attendanceId)
                },
                onError = {
                    Timber.tag(TAG).i("updateCheckIn() onError() onError: %s", it)

                }
            )
        }


    }
}