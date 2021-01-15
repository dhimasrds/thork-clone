package id.thork.app.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import id.thork.app.R

/**
 * Created by Dhimas Saputra on 14/01/21
 * Jakarta, Indonesia.
 */
class CustomDialogUtils(context: Context) : AlertDialog(context) {
    private val alertDialog :AlertDialog? = null
    private var listener : DialogActionListener? = null
    private var btnLeft : Button? = null
    private var btnRight : Button? = null
    private var btnMiddle : Button? = null

    interface DialogActionListener {
        fun onRightButton()
        fun onLeftButton()
        fun onMiddleButton()
    }


    init {
        setCancelable(false)
        val view = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog, null)
        setView(view)
        btnLeft = view.findViewById(R.id.btn_left)
        btnRight = view.findViewById(R.id.btn_right)
        btnMiddle = view.findViewById(R.id.btn_middle)
        if (alertDialog != null) {
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        btnLeft!!.setOnClickListener {
            listener!!.onLeftButton()
            alertDialog?.dismiss()
        }
    }

    private fun setClickListener(views: View) {

        btnRight!!.setOnClickListener {
            listener!!.onRightButton()
            alertDialog?.dismiss()
        }
        btnMiddle!!.setOnClickListener {
            listener!!.onMiddleButton()
            alertDialog?.dismiss()
        }
    }

    fun setLeftButtonText(text: Int) {
        val t = context.resources.getString(text)
//        layoutTwoButtons!!.visibility = View.VISIBLE
//        layoutOneButton!!.visibility = View.GONE
        btnLeft!!.visibility = View.VISIBLE
        btnLeft!!.text = t
    }

    fun setListener(listener: DialogActionListener?): CustomDialogUtils {
        this.listener = listener
        return this
    }
}