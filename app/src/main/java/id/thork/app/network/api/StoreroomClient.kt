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

import id.thork.app.network.ApiParam
import javax.inject.Inject

class StoreroomClient @Inject constructor(
    private val storeroomApi: StoreroomApi
) {

    suspend fun getStorerooms(cookie: String) =
        storeroomApi.getStorerooms(cookie, LEAN, ApiParam.API_SELECT_ALL)

    companion object {
        private const val LEAN = 1
    }
}