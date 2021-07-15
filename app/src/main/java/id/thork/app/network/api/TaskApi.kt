package id.thork.app.network.api

import com.skydoves.sandwich.ApiResponse
import id.thork.app.base.BaseParam
import id.thork.app.network.response.task_response.TaskResponse
import id.thork.app.network.response.work_order.Member
import retrofit2.http.*

/**
 * Created by Raka Putra on 7/5/21
 * Jakarta, Indonesia.
 */
interface TaskApi {

    @POST("/maximo/oslc/os/thisfsmwodetail/{workorderid}")
    suspend fun createTask(
        @Header(BaseParam.APP_X_METHOD_OVERRIDE) xMethodOverride: String?,
        @Header(BaseParam.APP_CONTENT_TYPE) contentType: String?,
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Header(BaseParam.APP_PATCHTYPE) patchType: String?,
        @Header(BaseParam.APP_PROPERTIES) properties: String?,
        @Path("workorderid") workorderid: Int,
        @Query(value = "lean") lean: Int,
        @Body body: TaskResponse
    ): ApiResponse<Member>

}