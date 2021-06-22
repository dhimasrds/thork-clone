package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.AttendanceEntity

/**
 * Created by M.Reza Sulaiman on 16/06/2021
 * Jakarta, Indonesia.
 */
interface AttendanceDao {
    fun remove()
    fun createAttendanceCache(
        attendanceEntity: AttendanceEntity,
        username: String?
    )

    fun findCheckInAttendance(): AttendanceEntity?
    fun findAttendanceBySynUpdate(syncUpdate: Int): AttendanceEntity?
    fun findAttendanceByAttendanceId(attendanceId: Int): AttendanceEntity?
    fun findListAttendanceOfflineMode(offlineMode: Int, syncUpdate: Int): List<AttendanceEntity>
    fun findListAttendanceLocal(): List<AttendanceEntity>
    fun findAttendanceByOfflinemode(offlinemode: Int): AttendanceEntity?
    fun filterByDate(startDate : Long, endDate : Long): List<AttendanceEntity>?
}