package id.thork.app.pages.main.element

import android.graphics.Color
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
                view.setTextColor(Color.YELLOW)
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
                view.setBackgroundColor(Color.YELLOW)
            }
            else -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorGreen))

            }
        }
    }
}