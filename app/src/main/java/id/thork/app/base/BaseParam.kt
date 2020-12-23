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
package id.thork.app.base

object BaseParam {
    const val BASE_SERVER_URL = "http://147.139.139.145:9080"

    const val APP_TRUE = 1
    const val APP_FALSE = 0

    const val APP_EMPTY_STRING = ""
    const val APP_DASH = "-"

    const val APP_DEFAULT_LANG = "eng"
    const val APP_DEFAULT_LANG_DETAIL = "English"
    const val APP_IND_LANG = "ind"
    const val APP_IND_LANG_DETAIL = "Indonesia"

    const val APP_LOGIN_PREFERENCE = "loginPreference"
    const val APP_SERVER_ADDRESS = "serverAddress"
    const val APP_FIRST_LAUNCH = "firstLaunch"

    /**
     * Authentication parameter
     */
    const val APP_MAX_AUTH = "MAXAUTH"
    const val APP_AUTHORIZATION = "AUTHORIZATION"
    const val APP_X_METHOD_OVERRIDE = "x-method-override"
    const val APP_PATCH = "PATCH"
    const val APP_MERGE = "MERGE"
    const val APP_SLUG = "Slug"
    const val APP_X_DOCUMENT_DESCRIPTION = "x-document-description"
    const val APP_X_DOCUMENT_META = "x-document-meta"
    const val APP_CONTENT_TYPE = "Content-Type"
    const val APP_PROPERTIES = "properties"
    const val APP_ALL_PROPERTIES = "*"
}
