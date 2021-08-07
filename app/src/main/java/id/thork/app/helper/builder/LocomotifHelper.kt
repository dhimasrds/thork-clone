package id.thork.app.helper.builder

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.*


class LocomotifHelper {

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
                Color.BLACK, // disabled
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
}