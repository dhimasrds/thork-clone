package id.thork.app.pages

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import id.thork.app.R
import id.thork.app.utils.StringUtils

/**
 * Created by Dhimas Saputra on 30/11/20
 * Jakarta, Indonesia.
 */
class CustomDialogUtils(context: Context) : AlertDialog(context) {
    private val alertDialog: AlertDialog? = null
    private var listener: DialogActionListener? = null
    private var tvTitle: TextView? = null
    private var tvDescription: TextView? = null
    private var btnLeft: Button? = null
    private var btnRight: Button? = null
    private var btnMiddle: Button? = null
    private var layoutOneButton: LinearLayout? = null
    private var layoutTwoButtons: LinearLayout? = null


    interface DialogActionListener {
        fun onRightButton()
        fun onLeftButton()
        fun onMiddleButton()
    }

    init {
        setCancelable(false)
        val view = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog, null)
        setView(view)
        tvTitle = view.findViewById(R.id.tv_title)
        tvDescription = view.findViewById(R.id.tv_desc)
        btnLeft = view.findViewById(R.id.btn_left)
        btnRight = view.findViewById(R.id.btn_right)
        btnMiddle = view.findViewById(R.id.btn_middle)
        layoutOneButton = view.findViewById(R.id.layout_one_button)
        layoutTwoButtons = view.findViewById(R.id.layout_two_button)

        if (alertDialog != null) {
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        btnLeft!!.setOnClickListener {
            listener!!.onLeftButton()
            alertDialog?.dismiss()
        }

        btnRight!!.setOnClickListener {
            listener!!.onRightButton()
            alertDialog?.dismiss()
        }

        btnMiddle!!.setOnClickListener {
            listener!!.onMiddleButton()
            alertDialog?.dismiss()
        }

    }

    fun setLeftButtonText(text: Int): CustomDialogUtils {
        val t = StringUtils.getStringResources(context, text)
        layoutTwoButtons!!.visibility = View.VISIBLE
        layoutOneButton!!.visibility = View.GONE
        btnLeft!!.visibility = View.VISIBLE
        btnLeft!!.text = t
        return this
    }

    fun setRightButtonText(text: Int): CustomDialogUtils {
        val t = StringUtils.getStringResources(context, text)
        layoutTwoButtons!!.visibility = View.VISIBLE
        layoutOneButton!!.visibility = View.GONE
        btnRight!!.visibility = View.VISIBLE
        btnRight!!.text = t
        return this
    }

    fun setMiddleButtonText(text: Int): CustomDialogUtils {
        val t = StringUtils.getStringResources(context, text)
        layoutTwoButtons!!.visibility = View.GONE
        layoutOneButton!!.visibility = View.VISIBLE
        btnMiddle!!.visibility = View.VISIBLE
        btnMiddle!!.text = t
        return this
    }

    fun setTittle(text: Int) : CustomDialogUtils {
        val t = StringUtils.getStringResources(context, text)
        tvTitle!!.text = t
        return this
    }

    fun setDescription(text: Int) : CustomDialogUtils {
        val t = StringUtils.getStringResources(context, text)
        tvDescription!!.text = t
        return this
    }

    fun setListener(listener: DialogActionListener?): CustomDialogUtils {
        this.listener = listener
        return this
    }
}