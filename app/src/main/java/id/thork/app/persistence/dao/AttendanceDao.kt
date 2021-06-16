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
}