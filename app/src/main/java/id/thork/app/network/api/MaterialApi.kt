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
import id.thork.app.network.model.material_actual.MatusetransBody
import retrofit2.http.*

/**
 * Created by Reja on 17/09/2021
 * Jakarta, Indonesia.
 */
interface MaterialApi {

    @POST("/maximo/oslc/os/thisfsmwodetail/{workorderid}")
    suspend fun addMaterialActual(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Header(BaseParam.APP_X_METHOD_OVERRIDE) xMethodeOverride: String?,
        @Header(BaseParam.APP_CONTENT_TYPE) contentType: String?,
        @Header(BaseParam.APP_PATCHTYPE) patchtype: String?,
        @Path("workorderid") workorderid: Int,
        @Query(value = "lean") lean: Int,
        @Body body: MatusetransBody?,
    ): ApiResponse<Void>

}