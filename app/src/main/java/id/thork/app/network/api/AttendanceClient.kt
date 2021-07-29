package id.thork.app.network.api

import id.thork.app.network.response.attendance_response.Member
import javax.inject.Inject

/**
 * Created by M.Reza Sulaiman on 16/06/2021
 * Jakarta, Indonesia.
 */
class AttendanceClient @Inject constructor(
    private val attendanceApi: AttendanceApi
) {
    suspend fun getListAttendance(
        cookie: String,
        select: String,
        where: String
    ) = attendanceApi.getListAttendance(cookie, LEAN, select, where)


    suspend fun createAttendance(
        cookie: String,
        contentType: String,
        properties: String,
        body: Member
    ) = attendanceApi.createAttendance(cookie, contentType, properties, LEAN, body)

    suspend fun updateAttendance(
        cookie: String,
        xMethodeOverride: String,
        contentType: String,
        patchType: String,
        attendanceId: Int,
        body: Member
    ) = attendanceApi.updateAttendance(
        cookie,
        xMethodeOverride,
        contentType,
        patchType,
        attendanceId,
        LEAN,
        body
    )

    suspend fun fetchAttendance(cookie: String, savedQuery: String, select: String) =
        attendanceApi.fetchAttendance(cookie, savedQuery, LEAN, select)

    companion object {
        private const val LEAN = 1
    }
}