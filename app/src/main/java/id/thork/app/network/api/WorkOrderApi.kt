package id.thork.app.network.api

import com.skydoves.sandwich.ApiResponse
import id.thork.app.base.BaseParam
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Dhimas Saputra on 12/01/21
 * Jakarta, Indonesia.
 */
interface WorkOrderApi {

    @GET("maximo/oslc/os/oslcwo")
    suspend fun getListWorkorder(
        @Query(value = "lean") lean: Int,
        @Query(value = "oslc.select") select: String?,
        @Query(value = "oslc.where") where: String?,
        @Query(value = "pageno") pageno: Int?,
        @Query(value = "oslc.pageSize") pagesize: Int?
    ): ApiResponse<WorkOrderResponse>

    @GET("maximo/oslc/os/oslcwo")
    suspend fun searchWorkorder(
        @Header("MAXAUTH") userHash: String?,
        @Query(value = "lean") lean: Int,
        @Query(value = "oslc.select") select: String?,
        @Query(value = "oslc.where") where: String?
    ): ApiResponse<WorkOrderResponse>

    @POST("/maximo/oslc/os/THISFSMWODETAIL?lean=1")
    suspend fun createWO(
        @Header(BaseParam.APP_MAX_AUTH) maxAuth: String?,
//        @Header(BaseParam.APP_PROPERTIES) properties: String?,
//        @Header(BaseParam.APP_CONTENT_TYPE) contentType: String?,
        @Body body: Member?
    ): ApiResponse<Member>

    @POST("/maximo/oslc/os/thisfsmwodetail/{workorderid}")
    suspend fun updateStatus(
        @Header(BaseParam.APP_MAX_AUTH) maxAuth: String?,
        @Header(BaseParam.APP_X_METHOD_OVERRIDE) xMethodeOverride: String?,
        @Header(BaseParam.APP_CONTENT_TYPE) contentType: String?,
        @Path("workorderid") workorderid: Int,
        @Query(value = "lean") lean: Int,
        @Body body: Member?
    ): ApiResponse<WorkOrderResponse>
}