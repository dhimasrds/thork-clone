/*
 * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * This.ID. ("Confidential Information").
 *
 * Such Confidential Information shall not be disclosed and shall
 * use it only	 in accordance with the terms of the license agreement
 * entered into with This.ID; other than in accordance with the written
 * permission of This.ID.
 */

package id.thork.app.network.api

import com.skydoves.sandwich.ApiResponse
import id.thork.app.base.BaseParam
import id.thork.app.network.response.material_response.MaterialResponse
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.network.response.work_order.doclinks.DoclinksMember
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by Reja on 28/05/21
 * Jakarta, Indonesia.
 */
interface MaterialApi {

    @GET("maximo/oslc/os/THISFSMITEM")
    suspend fun getMaterials(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Query(value = "lean") lean: Int
    ): ApiResponse<MaterialResponse>
}