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

import javax.inject.Inject

class LoginClient @Inject constructor(
    private val loginApi: LoginApi,
) {

    suspend fun loginByPerson(select: String, where: String) =
        loginApi.loginByPerson(LEAN, select, where)

    suspend fun login(maxauth: String) = loginApi.login(maxauth)

    suspend fun logout(cookie: String, maxauth: String) = loginApi.logout(cookie, maxauth)

    suspend fun getSystemProperties(headerParam: String, select: String) =
        loginApi.getSystemProperties(headerParam, LEAN, select)

    companion object {
        private const val LEAN = 1
    }
}