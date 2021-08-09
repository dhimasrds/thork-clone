package id.thork.app.helper.builder

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity

import android.util.DisplayMetrics





class LocomotifHelper {

    fun getScreenWidth(context: Context): Int {
//        val displayMetrics = DisplayMetrics()
//        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
//        return displayMetrics.widthPixels

        val outMetrics = DisplayMetrics()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display =  (context as Activity).display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display =  (context as Activity).windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }
        return outMetrics.widthPixels
    }

    fun fetchAccentColor(context: Context): Int {
        val typedValue = TypedValue()
        val a: TypedArray =
            context.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    fun fetchPrimaryColor(context: Context): Int {
        val typedValue = TypedValue()
        val a: TypedArray =
            context.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorPrimary))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    fun fetchDefaultColorStateList(context: Context): ColorStateList {
        val colorStateList = ColorStateList(
            arrayOf(intArrayOf(-R.attr.state_enabled), intArrayOf(R.attr.state_enabled)),
            intArrayOf(
                getPrimaryColor(context), // disabled
                getPrimaryColor(context)
            )
        )
        return colorStateList
    }

    private fun getPrimaryColor(context: Context): Int {
        val value = TypedValue()
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true)
        return value.data
    }

    fun getAppDateFormat(dateString: String?): Date {
        val APP_DATE_FORMAT = "dd/MM/yyyy"
        val date = SimpleDateFormat(APP_DATE_FORMAT).parse(dateString)
        return date
    }

    fun NVL(originText: String?, replacementText: String): String {
        return if (originText != null && !originText.isEmpty()) {
            originText
        } else replacementText
    }

    fun convertNullToEmpty(originText: String): String {
        return if (originText != null && !originText.isEmpty() && originText.equals("null")) {
            ""
        }else if (originText != null && !originText.isEmpty()) {
            originText
        } else ""
    }

    fun convertNullToZero(originText: String): String {
        return if (originText != null && !originText.isEmpty() && originText.equals("null")) {
            "0"
        }else if (originText != null && !originText.isEmpty()) {
            originText
        } else "0"
    }
}