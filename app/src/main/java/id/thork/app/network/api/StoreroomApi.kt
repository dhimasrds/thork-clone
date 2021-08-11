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
import id.thork.app.network.response.storeroom_response.StoreroomResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Created by Reja on 28/05/21
 * Jakarta, Indonesia.
 */
interface StoreroomApi {
    @GET("maximo/oslc/os/THISFSMSTOREROOM?savedQuery=THISFSMMOBILESTORE")
    suspend fun getStorerooms(
        @Header(BaseParam.APP_MX_COOKIE) cookie: String?,
        @Query(value = "lean") lean: Int,
        @Query(value = "oslc.select") select: String,
    ): ApiResponse<StoreroomResponse>
}