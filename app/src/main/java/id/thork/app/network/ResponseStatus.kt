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

package id.thork.app.network

class ResponseStatus {
    companion object Constants {
        const val HTTP_SUCCESS = "SUCCESS"
        const val HTTP_ERROR = "ERROR"

        const val HTTP_STATUS_OK = 200
        const val HTTP_STATUS_CREATED = 201
        const val HTTP_STATUS_ACCEPTED = 202
        const val HTTP_STATUS_NO_AUTHORITATIVE = 203
        const val HTTP_STATUS_NO_CONTENT = 204
        const val HTTP_STATUS_RESET_CONTENT = 205

        const val HTTP_STATUS_BAD_REQUEST = 400
        const val HTTP_STATUS_UNAUTHORIZED = 401
        const val HTTP_STATUS_PAYMENT_REQUIRED = 402
        const val HTTP_STATUS_FORBIDDEN = 403
        const val HTTP_STATUS_NOT_FOUND = 404
        const val HTTP_STATUS_METHOD_NOT_ALLOWED = 405
        const val HTTP_STATUS_NOT_ACCEPTABLE = 406

        fun getStatus(statusCode: Int): String {
            when (statusCode) {
                HTTP_STATUS_OK, HTTP_STATUS_CREATED, HTTP_STATUS_ACCEPTED,
                HTTP_STATUS_NO_AUTHORITATIVE, HTTP_STATUS_NO_CONTENT, HTTP_STATUS_RESET_CONTENT -> {
                    return HTTP_SUCCESS
                }
                HTTP_STATUS_BAD_REQUEST, HTTP_STATUS_UNAUTHORIZED, HTTP_STATUS_PAYMENT_REQUIRED, HTTP_STATUS_FORBIDDEN,
                HTTP_STATUS_NOT_FOUND, HTTP_STATUS_METHOD_NOT_ALLOWED, HTTP_STATUS_NOT_ACCEPTABLE -> {
                    return HTTP_ERROR
                }
            }
            return HTTP_ERROR
        }

        fun isHttpSuccess(statusCode: Int): Boolean {
            when (statusCode) {
                HTTP_STATUS_OK, HTTP_STATUS_CREATED, HTTP_STATUS_ACCEPTED,
                HTTP_STATUS_NO_AUTHORITATIVE, HTTP_STATUS_NO_CONTENT, HTTP_STATUS_RESET_CONTENT -> {
                    return true
                }
                HTTP_STATUS_BAD_REQUEST, HTTP_STATUS_UNAUTHORIZED, HTTP_STATUS_PAYMENT_REQUIRED, HTTP_STATUS_FORBIDDEN,
                HTTP_STATUS_NOT_FOUND, HTTP_STATUS_METHOD_NOT_ALLOWED, HTTP_STATUS_NOT_ACCEPTABLE -> {
                    return false
                }
            }
            return false
        }
    }
}