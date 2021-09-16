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

import id.thork.app.base.BaseParam
import id.thork.app.network.ApiParam
import id.thork.app.network.model.material_actual.MatusetransBody
import javax.inject.Inject

class MaterialClient @Inject constructor(
    private val materialApi: MaterialApi
) {

    suspend fun addMaterialActual(cookie: String, workorderId: Int, matusetransBody: MatusetransBody) =
        materialApi.addMaterialActual(cookie, BaseParam.APP_PATCH, BaseParam.APP_CONTENT_TYPE_JSON,
            BaseParam.APP_MERGE, workorderId, LEAN, matusetransBody)

    companion object {
        private const val LEAN = 1
    }
}