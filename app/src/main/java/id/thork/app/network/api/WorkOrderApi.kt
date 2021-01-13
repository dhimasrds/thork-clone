package id.thork.app.network.api

import com.skydoves.sandwich.ApiResponse
import id.thork.app.network.response.work_order.WorkOrderResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Created by Dhimas Saputra on 12/01/21
 * Jakarta, Indonesia.
 */
interface WorkOrderApi {

    @GET("maximo/oslc/os/oslcwo")
    fun getListWorkorder(
        @Header("MAXAUTH") userHash: String?,
        @Query(value = "lean") lean: Int,
        @Query(value = "oslc.select") select: String?,
        @Query(value = "oslc.where") where: String?
    ): ApiResponse<WorkOrderResponse>
}