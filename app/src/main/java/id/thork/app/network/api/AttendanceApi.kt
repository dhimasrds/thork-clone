package id.thork.app.network.api

import com.skydoves.sandwich.ApiResponse
import id.thork.app.base.BaseParam
import id.thork.app.network.response.attendance_response.AttendanceResponse
import id.thork.app.network.response.attendance_response.Member
import retrofit2.http.*

/**
 * Created by M.Reza Sulaiman on 16/06/2021
 * Jakarta, Indonesia.
 */
interface AttendanceApi {

    @GET("maximo/oslc/os/THISFSMATTENDANCE")
    suspend fun getListAttendance(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Query(value = "lean") lean: Int,
        @Query(value = "oslc.select") select: String?,
        @Query(value = "oslc.where") where: String?
    ): ApiResponse<AttendanceResponse>

    @POST("maximo/oslc/os/THISFSMATTENDANCE")
    suspend fun createAttendance(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Header(BaseParam.APP_CONTENT_TYPE) contentType: String?,
        @Header(BaseParam.APP_PROPERTIES) properties: String?,
        @Query(value = "lean") lean: Int,
        @Body body: Member
    ): ApiResponse<Member>


    @POST("maximo/oslc/os/THISFSMATTENDANCE/{attendanceid}")
    suspend fun updateAttendance(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Header(BaseParam.APP_X_METHOD_OVERRIDE) xMethodeOverride: String?,
        @Header(BaseParam.APP_CONTENT_TYPE) contentType: String?,
        @Header(BaseParam.APP_PATCHTYPE) patchtype: String?,
        @Path("attendanceid") attendanceid: Int,
        @Query(value = "lean") lean: Int,
        @Body body: Member
    ): ApiResponse<Void>

    @GET("maximo/oslc/os/THISFSMATTENDANCE")
    suspend fun fetchAttendance(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Query("savedQuery") savedQuery: String?,
        @Query(value = "lean") lean: Int,
        @Query(value = "oslc.select") select: String?,
        ) : ApiResponse<AttendanceResponse>
}