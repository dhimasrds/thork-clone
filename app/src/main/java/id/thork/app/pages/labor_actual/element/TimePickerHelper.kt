package id.thork.app.pages.labor_actual.element

import android.app.TimePickerDialog
import android.content.Context
import id.thork.app.R
import java.util.*

/**
 * Created by Dhimas Saputra on 02/08/21
 * Jakarta, Indonesia.
 */
class TimePickerHelper(
    context: Context,
    is24HourView: Boolean,
    isSpinnerType: Boolean = false
) {
    private var dialog: TimePickerDialog
    private var callback: Callback? = null
    private val listener = TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
        callback?.onTimeSelected(hourOfDay, minute)
    }
    init {
        val style = if (isSpinnerType) R.style.SpinnerTimePickerDialogSpinner else R.style.SpinnerTimePickerDialog
        val cal = Calendar.getInstance()
        dialog = TimePickerDialog(context, style, listener,
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), is24HourView)
    }
    fun showDialog(hourOfDay: Int, minute: Int, callback: Callback?) {
        this.callback = callback
        dialog.updateTime(hourOfDay, minute)
        dialog.show()
    }
    interface Callback {
        fun onTimeSelected(hourOfDay: Int, minute: Int)
    }
}
