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
    const val BASE_SERVER_URL = "http://149.129.252.41:9080"

    /**
     * Firebase Parameter
     */
    const val FIREBASE_TOPIC = "/topics/"
    const val FIREBASE_NOTIFICATION_TOPIC = "thor-notif"
    const val FIREBASE_LOCATION_TOPIC = "thor-location"

    const val APP_TRUE = 1
    const val APP_FALSE = 0

    const val APP_TRUE_STRING = "true"
    const val APP_FALSE_STRING = "false"

    const val APP_EMPTY_STRING = ""
    const val APP_DASH = "-"

    const val APP_DEFAULT_LANG = "eng"
    const val APP_VERSION = "Version "

    const val APP_DEFAULT_LANG_DETAIL = "English"
    const val APP_IND_LANG = "ind"
    const val APP_IND_LANG_DETAIL = "Indonesia"
    const val SELECTED_LANG_DETAIL = "LANG"
    const val SELECTED_LANG_CODE = "LANG-CODE"

    const val APP_LOGIN_PREFERENCE = "loginPreference"
    const val APP_SERVER_ADDRESS = "serverAddress"
    const val APP_FIRST_LAUNCH = "firstLaunch"
    const val REPORT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX"
    const val APP_MX_COOKIE = "cookie"

    /**
     * Authentication parameter
     */
    const val APP_MAX_AUTH = "MAXAUTH"
    const val APP_COOKIE = "Cookie"
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

    const val ID = "ID"

    /**
     * Intent message key
     */
    const val INTENT_PERSON_UID = "personUID"
    const val INTENT_VALIDATE_PATTERN = "validatePattern"
    const val TAG_SETTING = "TAG_SETTING"

    /**
     *  Workorder status Param
     *
     */
    const val APPROVED = "APPR"
    const val WAPPR = "WAPPR"
    const val WASGN = "WASGN"
    const val INPROGRESS = "INPRG"
    const val COMPLETED = "COMP"
    const val OPERATING = "OPERATING"
    const val CLOSED = "CLOSE"
    const val DISPATCH = "DISPATCH"
    const val ACCEPT = "ACCEPT"
    const val ARRIVED = "ARRIVE"
    const val REASSGN = "REASSGN"
    const val WSUPP = "WSUPP"
    const val DONE = "DONE"

    /**
     * Base Parameter Google Maps
     */
    const val APP_WONUM = "WO. Number "
    const val APP_DETAIL = "Detail"
    const val APP_CREW = "Crew "
    const val APP_MOVE = "move"
    const val APP_TAG_MARKER_WO = "WO"
    const val APP_TAG_MARKER_LOCATION = "LOCATION"
    const val APP_TAG_MARKER_ASSET = "ASSET"
    const val APP_TAG_MARKER_CREW = "CREW"
    const val APP_TAG_MARKER_CREW_MOVE = "move"
    const val APP_TAG_MARKER_CREW_LOGOUT = "logout"
    const val APP_LOCATION_LONGITUDE = "LONGITUDE"
    const val APP_LOCATION_LATITUDE = "LATITUDE"

    /**
     * Base Parameter Priority Workorder
     */
    const val PRIORITY_NORMAL_DESC = "Normal"
    const val PRIORITY_MEDIUM_DESC = "Medium"
    const val PRIORITY_HIGH_DESC = "High"


    /**
     * Base Parameter Google Maps Direction Route
     */
    const val APP_APIKEY = "AIzaSyBPSt1F9lZcgia7JG4nW5eFPAuJCv68XBQ"
    const val BASE_MAPS_URL = "https://maps.googleapis.com/maps/api/directions/"

    /**
     * Base Parameter Firebase
     */
    const val BASE_FIREBASE_URL = "https://fcm.googleapis.com/"
    const val APP_APIKEY_FIREBASE =
        "key=AAAA1bjwolE:APA91bHxc-tlmJw_Slov7QEUECjL6JX3C9sh5njm8kFnXDqH0zrGrB34gnZbWcTjcw6qnq66mdoKeK8kWPaIS1YvHKK4P1qV1N5esT2bdYzLsketNIQ3_tZnw5Na0n0JBfZc9BBhkF2I"
    const val APP_PRIORITY = "high"
    const val APP_FIREBASE_TOPIC = "/topics/thor-location"

    /**
     * Workorder Adapter Param
     *
     */
    const val WONUM = "Wonum"
    const val DESCRIPTION = "Description"
    const val DATE = "Date"
    const val HOURSSTART = "Hours start"
    const val HOURSFINISH = "Hours Finish"
    const val PERIOD = "Period"
    const val SUPERVISOR = "Supervisor"
    const val LOCATIONS = "Location"
    const val LISTITEM = "List Item"
    const val WORKORDERID = "WORKORDERID"
    const val STATUS = "STATUS"
    const val LATITUDE = "SERVICEADDRESS_latitude"
    const val LONGITUDE = "SERVICEADDRESS_longitude"
    const val STREETADDRESS = "SERVICEADDRESS_streetAddress"
    const val PRIORITY_LEVEL = "PRIORITY_LEVEL"
    const val WORK_CATEGORY = "WORK_CATEGORY"
    const val ESTDUR = "ESTDUR"
    const val REPORT_DATE = "REPORT_DATE"
    const val PRIORITY_NORMAL = "0"
    const val PRIORITY_MEDIUM = "1"
    const val PRIORITY_HIGH = "2"
    const val LONGDESC = "longdescription"

    /**
     * Status Code
     *
     */
    const val STATUS_UNAUTHORIZED = "401"
    const val STATUS_OK = "200"
    const val STATUS_NOTFOUND = "404"

}
