package id.thork.app.helper.builder

import android.R
import android.content.Context

import android.content.res.TypedArray

import android.util.TypedValue
import android.content.res.ColorStateList
import android.graphics.Color

import android.os.Build


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

    fun fetchDefaultColorStateList(): ColorStateList {
        val colorStateList = ColorStateList(
            arrayOf(intArrayOf(-R.attr.state_enabled), intArrayOf(R.attr.state_enabled)),
            intArrayOf(
                Color.BLACK, // disabled
                Color.BLUE
            )
        )
        return colorStateList
    }

}