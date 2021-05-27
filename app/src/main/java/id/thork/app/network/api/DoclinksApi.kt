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
import id.thork.app.network.response.work_order.doclinks.DoclinksMember
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by Reja on 24/05/21
 * Jakarta, Indonesia.
 */
interface DoclinksApi {
    @GET
    suspend fun getDoclinks(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Url url: String): ApiResponse<DoclinksMember>

    @GET
    suspend fun getDoclinksHref(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Url url: String): ApiResponse<ResponseBody>

    @POST("maximo/oslc/os/thisfsmwodetail/{woId}/doclinks")
    suspend fun uploadAttachment(
        @Path("woId") woId: Int,
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Header(BaseParam.APP_SLUG) slug: String?,
        @Header(BaseParam.APP_X_DOCUMENT_META) documentMeta: String?,
        @Header(BaseParam.APP_X_DOCUMENT_DESCRIPTION) documentDesc: String?,
        @Header(BaseParam.APP_CONTENT_TYPE) contentType: String?,
        @Body imageFile: RequestBody): ApiResponse<Void>

}