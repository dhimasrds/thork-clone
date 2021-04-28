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

import id.thork.app.network.model.user.TokenApikey
import javax.inject.Inject

class LoginClient @Inject constructor(
    private val loginApi: LoginApi
) {

    suspend fun loginByPerson(apikey: String, select: String, where: String) =
        loginApi.loginByPerson(apikey, LEAN, select, where)


    suspend fun createTokenApiKey(headerParam: String, contentType: String, body: TokenApikey)=
        loginApi.createTokenApi(headerParam, contentType, body)

    suspend fun getSystemProperties(headerParam: String, select: String) =
        loginApi.getSystemProperties(headerParam, LEAN, select)

    companion object {
        private const val LEAN = 1
    }
}