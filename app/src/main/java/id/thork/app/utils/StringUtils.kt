/*
 * *
 *  * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved. <BR>
 *  * <BR>
 *  * This software is the confidential and proprietary information of
 *  * This.ID. ("Confidential Information").<BR>
 *  * <BR>
 *  * Such Confidential Information shall not be disclosed and shall
 *  * use it only	 in accordance with the terms of the license agreement
 *  * entered into with This.ID; other than in accordance with the written
 *  * permission of This.ID. <BR>
 *  *
 *
 *
 */
package id.thork.app.utils

import android.annotation.SuppressLint
import android.content.Context
import id.thork.app.base.BaseParam
import java.io.File
import java.util.*

/**
 * Created by Reja on 17,January,2019
 * Jakarta, Indonesia.
 */
object StringUtils {
    /**
     * Get String resources value
     *
     * @param context
     * @param stringResource
     * @return
     */
    fun getStringResources(context: Context, stringResource: Int): String {
        return context.resources.getString(stringResource)
    }


    /**
     * Encode string to base64
     *
     * @param string
     * @return
     */
    @SuppressLint("NewApi")
    fun encodeToBase64(string: String): String? {
        return Base64.getEncoder().encodeToString(string.toByteArray())
    }

    @SuppressLint("NewApi")
    fun decodeToBase64(string: String): String {
        val decodedBytes = Base64.getDecoder().decode(string)
        return String(decodedBytes)
    }


    fun generateUUID(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    /**
     * String replacement if origin text is null or is empty
     *
     * @param originText
     * @param replacementText
     * @return
     */
    fun NVL(originText: String?, replacementText: String): String {
        return if (originText != null && !originText.isEmpty()) {
            originText
        } else replacementText
    }

    fun NVL(originText: Int?, replacementText: Int): Int {
        return originText?: replacementText
    }

    fun createPriority(priority: Int): String {
        if (priority == 1) {
            return BaseParam.PRIORITY_NORMAL_DESC
        } else if (priority == 2) {
            return BaseParam.PRIORITY_MEDIUM_DESC
        } else if (priority == 3) {
            return BaseParam.PRIORITY_HIGH_DESC
        }
        return BaseParam.APP_DASH
    }
    //TODO
//    fun getBundleString(bundle: Bundle?, key: String?, replacementValue: String): String {
//        return if (bundle != null && bundle.getString(key) != null && !bundle.getString(key)
//                .isEmpty()
//        ) {
//            bundle.getString(key)
//        } else {
//            replacementValue
//        }
//    }

    fun getFileExtension(file: File): String {
        val fileName = file.name
        return if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) fileName.substring(
            fileName.lastIndexOf(".") + 1
        ) else ""
    }

    fun truncate(text: String?, maxWidth: Int): String {
        return if (text != null && !text.isEmpty()) {
            if (text.length > maxWidth) {
                text.substring(0, Math.min(text.length, maxWidth)) + "..."
            } else {
                text
            }
        } else ""
    }

    //TODO
//    fun generateSerialNumber(): String {
//        try {
//            val r = Random()
//            val prefix = r.nextInt(99999999 - 10000000 + 1) + 10000000
//            val refs = r.nextInt(9999 - 1000 + 1) + 1000
//            val startLong = 10000000L
//            val endLong = 99999999999L
//            val serial = ThreadLocalRandom.current().nextLong(startLong, endLong)
//            val parseSGTIN: ParseSGTIN = ParseSGTIN.Builder()
//                .withCompanyPrefix(prefix.toString() + "")
//                .withExtensionDigit(SGTINExtensionDigit.EXTENSION_2)
//                .withItemReference(refs.toString() + "")
//                .withSerial(serial.toString() + "")
//                .withTagSize(SGTINTagSize.BITS_96)
//                .withFilterValue(SGTINFilterValue.RESERVED_3)
//                .build()
//            val ss: SGTIN = parseSGTIN.getSGTIN()
//            return ss.getRfidTag()
//        } catch (e: Exception) {
//            Log.d("getAvailableSerial: %s", e.message!!)
//        }
//        return BaseParam.EMPTY_STRING
//    }

    /**
     * Check origintext is null or is empty
     *
     * @param originText
     * @return
     */
    fun isNull(originText: String?): Boolean {
        return !(originText != null && !originText.isEmpty())
    }


    fun isValidString(string: String?): Boolean {
        return string != null && !string.isEmpty()
    }

    fun convertTimeString(string: String): String? {
        var string = string
        if (string.length < 2) {
            string = "0$string"
        }
        return string
    }
}