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
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

/**
 * Created by Reja on 24/05/21
 * Jakarta, Indonesia.
 */
interface DoclinksApi {
    @GET
    suspend fun getDoclinks(
        @Header(BaseParam.APP_COOKIE) cookie: String?,
        @Url url: String): ApiResponse<DoclinksMember>
}