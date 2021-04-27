package id.thork.app.utils

import android.text.InputFilter
import android.text.Spanned
import id.thork.app.base.BaseParam

/**
 * Created by Raka Putra on 3/15/21
 * Jakarta, Indonesia.
 */
class InputFilterMinMaxUtils constructor(private var min: Int, private var max: Int): InputFilter {
//    private var min:Int? = null
//    private var max:Int? = null

//    fun InputFilterMinMaxUtils(min: Int, max: Int) {
//        this.min = min
//        this.max = max
//    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toInt()
            if (isInRange(min, max, input)) return null
        } catch (nfe: NumberFormatException) {
        }
        return BaseParam.APP_EMPTY_STRING
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c >= a && c <= b else c >= b && c <= a
    }
}