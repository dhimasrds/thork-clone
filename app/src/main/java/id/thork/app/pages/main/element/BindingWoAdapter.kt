package id.thork.app.pages.main.element

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.utils.StringUtils
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

            BaseParam.CLOSED -> {
                view.text = BaseParam.CLOSED
                view.setTextColor(ContextCompat.getColor(view.context,R.color.colorGray2))
                view.setBackgroundResource(R.drawable.bg_status_grey)
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
            BaseParam.COMPLETED -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorGreen))
            }
            BaseParam.CLOSED -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorGray2))

            }
        }
    }

    @BindingAdapter("setPriority")
    @JvmStatic fun setPriority(view: TextView, priority: Int) {
        Timber.d("setStatus :%s", priority)
        val convertWopriority = StringUtils.createPriority(priority)
        when (convertWopriority) {
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