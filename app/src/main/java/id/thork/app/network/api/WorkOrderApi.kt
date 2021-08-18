package id.thork.app.network.api

import com.skydoves.sandwich.ApiResponse
import id.thork.app.base.BaseParam
import id.thork.app.network.response.asset_response.AssetResponse
import id.thork.app.network.response.fsm_location.FsmLocation
import id.thork.app.network.response.material_response.MaterialResponse
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.network.response.worklogtype_response.WorklogtypeResponse
import retrofit2.http.*

/**
 * Created by Dhimas Saputra on 12/01/21
 * Jakarta, Indonesia.
 */
interface WorkOrderApi {

    @GET("maximo/oslc/os/THISFSMWODETAIL")
    suspend fun getListWorkorder(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Query(value = "lean") lean: Int,
        @Query("savedQuery") savedQuery: String?,
        @Query(value = "oslc.select") select: String?,
        @Query(value = "pageno") pageno: Int?,
        @Query(value = "oslc.pageSize") pagesize: Int?,
    ): ApiResponse<WorkOrderResponse>

    @GET("maximo/oslc/os/oslcwo")
    suspend fun searchWorkorder(
        @Header("MAXAUTH") userHash: String?,
        @Query(value = "lean") lean: Int,
        @Query(value = "oslc.select") select: String?,
        @Query(value = "oslc.where") where: String?,
    ): ApiResponse<WorkOrderResponse>

    @POST("/maximo/oslc/os/THISFSMWODETAIL?lean=1")
    suspend fun createWO(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Header(BaseParam.APP_PROPERTIES) properties: String?,
        @Body body: Member?,
    ): ApiResponse<Member>

    @POST("/maximo/oslc/os/thisfsmwodetail/{workorderid}")
    suspend fun updateStatus(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Header(BaseParam.APP_X_METHOD_OVERRIDE) xMethodeOverride: String?,
        @Header(BaseParam.APP_CONTENT_TYPE) contentType: String?,
        @Header(BaseParam.APP_PATCHTYPE) patchtype: String?,
        @Path("workorderid") workorderid: Int,
        @Query(value = "lean") lean: Int,
        @Body body: Member?,
    ): ApiResponse<WorkOrderResponse>

    //TODO MUST MIGRATE AFTER POC

    @GET("maximo/oslc/os/thisfsmlocations")
    suspend fun getLocationResource(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Query(value = "lean") lean: Int,
        @Query("savedQuery") savedQuery: String?,
        @Query(value = "oslc.select") select: String?,
    ): ApiResponse<FsmLocation>

    @GET("/maximo/oslc/os/thisfsmasset?lean=1")
    suspend fun getListAsset(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Query("savedQuery") savedQuery: String?,
        @Query(value = "oslc.select") select: String?,
    ): ApiResponse<AssetResponse>

    @GET("maximo/oslc/os/THISFSMITEM")
    suspend fun getItemMaster(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Query(value = "lean") lean: Int,
        @Query(value = "oslc.select") select: String?,
    ): ApiResponse<MaterialResponse>

    @GET("maximo/oslc/os/THISFSMSYNDOMAIN")
    suspend fun getWorklogType(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Query(value = "lean") lean: Int,
        @Query(value = "oslc.select") select: String?,
        @Query(value = "oslc.where") where: String?
    ): ApiResponse<WorklogtypeResponse>

    //POST LABOR PLAN
    @POST("/maximo/oslc/os/thisfsmwodetail/{workorderid}")
    suspend fun addLaborPlan(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Header(BaseParam.APP_X_METHOD_OVERRIDE) xMethodeOverride: String?,
        @Header(BaseParam.APP_CONTENT_TYPE) contentType: String?,
        @Header(BaseParam.APP_PATCHTYPE) patchtype: String?,
        @Path("workorderid") workorderid: Int,
        @Query(value = "lean") lean: Int,
        @Body body: Member?,
    ): ApiResponse<WorkOrderResponse>


    // UPDATE LABOR PLAN
    @PUT("/maximo/oslc/os/thisfsmwodetail/{workorderid}")
    suspend fun updateLaborPlan(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Header(BaseParam.APP_X_METHOD_OVERRIDE) xMethodeOverride: String?,
        @Header(BaseParam.APP_CONTENT_TYPE) contentType: String?,
        @Header(BaseParam.APP_PATCHTYPE) patchtype: String?,
        @Path("workorderid") workorderid: Int,
        @Query(value = "lean") lean: Int,
        @Body body: Member?,
    ): ApiResponse<WorkOrderResponse>


}