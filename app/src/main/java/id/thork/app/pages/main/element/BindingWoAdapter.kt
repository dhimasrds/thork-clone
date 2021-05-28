package id.thork.app.pages.main.element

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import id.thork.app.R
import id.thork.app.base.BaseParam
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 18/01/21
 * Jakarta, Indonesia.
 */

object BindingWoAdapter {
    @BindingAdapter("setStatus")
    @JvmStatic fun setStatus(view: TextView, status: String?) {
        Timber.d("setStatus :%s", status)
        when (status) {
            BaseParam.APPROVED -> {
                view.text = BaseParam.APPROVED
                view.setTextColor(ContextCompat.getColor(view.context, R.color.blueTextStatus))
                view.setBackgroundResource(R.drawable.bg_status)

            }
            BaseParam.INPROGRESS -> {
                view.text = BaseParam.INPROGRESS
                view.setTextColor(ContextCompat.getColor(view.context, R.color.colorYellow))
                view.background = ContextCompat.getDrawable(view.context,R.drawable.bg_status_yellow)
            }
            BaseParam.COMPLETED -> {
                view.text = BaseParam.COMPLETED
                view.setTextColor(ContextCompat.getColor(view.context,R.color.colorGreen))
                view.setBackgroundResource(R.drawable.bg_status_green)
            }
        }
    }

    @BindingAdapter("setBgStatus")
    @JvmStatic fun setBgStatus(view: View, status: String?) {
        when (status) {
            BaseParam.APPROVED -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.blueTextStatus))
            }
            BaseParam.INPROGRESS -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorYellow))
            }
            else -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorGreen))

            }
        }
    }

    @BindingAdapter("setPriority")
    @JvmStatic fun setPriority(view: TextView, priority: String?) {
        Timber.d("setStatus :%s", priority)
        when (priority) {
            BaseParam.NORMAL -> {
                view.text = BaseParam.NORMAL
                view.setTextColor(ContextCompat.getColor(view.context, R.color.priority_normal))
                view.setBackgroundResource(R.drawable.bg_priority_normal)

            }
            BaseParam.MEDIUM -> {
                view.text = BaseParam.MEDIUM
                view.setTextColor(ContextCompat.getColor(view.context, R.color.priority_normal))
                view.background = ContextCompat.getDrawable(view.context,R.drawable.bg_priority_medium)
            }
            BaseParam.HIGH -> {
                view.text = BaseParam.HIGH
                view.setTextColor(ContextCompat.getColor(view.context,R.color.priority_high))
                view.setBackgroundResource(R.drawable.bg_priority_high)
            }
        }
    }
}