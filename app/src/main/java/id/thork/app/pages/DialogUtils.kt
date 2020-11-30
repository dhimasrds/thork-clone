package id.thork.app.pages

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog

/**
 * Created by Dhimas Saputra on 30/11/20
 * Jakarta, Indonesia.
 */

class DialogUtils {
    interface DialogUtilsListener {
        fun onPositiveButton()
        fun onNegativeButton()
    }

    var context: Context
    private var theme = 0
    var title = 0
        private set
    var message = 0
        private set
    private var positiveButtonLabel: Int? = null
    private var negativeButtonLabel: Int? = null
    var listener: DialogUtilsListener? = null
        private set
    var isPositiveButton = false
        private set
    var isNegativeButton = false
        private set
    var isCanceable = false
        private set
    var builder: AlertDialog.Builder? = null
        private set
    private var dialogView: View? = null
    private var inflater: LayoutInflater? = null
    private var dialog: AlertDialog? = null

    constructor(context: Context) {
        this.context = context
        if (builder == null) {
            builder = AlertDialog.Builder(context)
        }
    }

    constructor(context: Context, theme: Int) {
        this.context = context
        this.theme = theme
        if (builder == null) {
            builder = AlertDialog.Builder(context, theme)
        }
    }

    fun show() {
//        Timber.tag(TAG).i("DialogUtils.show()")
        dialog = builder!!.show()
    }

    fun setRounded(rounded: Boolean) {
        if (rounded) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun setTitle(title: Int): DialogUtils {
        this.title = title
        builder!!.setTitle(title)
        return this
    }

    fun setViewId(id: Int): View {
        return dialogView!!.findViewById(id)
    }

    fun setInflater(resource: Int, root: ViewGroup?, layoutInflater: LayoutInflater?): DialogUtils {
        inflater = layoutInflater
        dialogView = inflater!!.inflate(resource, root)
        builder!!.setView(dialogView)
        return this
    }

    fun setMessage(message: Int): DialogUtils {
        this.message = message
        builder!!.setMessage(message)
        return this
    }

    fun getPositiveButtonLabel(): Int {
        return positiveButtonLabel!!
    }

    fun setPositiveButtonLabel(positiveButtonLabel: Int?): DialogUtils {
        this.positiveButtonLabel = positiveButtonLabel
        if (positiveButtonLabel == null) {
            builder!!.setPositiveButton(null, null)
            isPositiveButton = false
        } else {
            builder!!.setPositiveButton(positiveButtonLabel, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    if (isPositiveButton && listener != null) {
                        listener!!.onPositiveButton()
                    }
                }
            })
            isPositiveButton = true
        }
        return this
    }

    fun getNegativeButtonLabel(): Int {
        return negativeButtonLabel!!
    }

    fun setNegativeButtonLabel(negativeButtonLabel: Int?): DialogUtils {
        this.negativeButtonLabel = negativeButtonLabel
        if (negativeButtonLabel == null) {
            builder!!.setNegativeButton(null, null)
            isNegativeButton = false
        } else {
            builder!!.setNegativeButton(negativeButtonLabel, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    if (isNegativeButton && listener != null) {
                        listener!!.onNegativeButton()
                    }
                }
            })
            isNegativeButton = true
        }
        return this
    }

    fun setListener(listener: DialogUtilsListener?): DialogUtils {
        this.listener = listener
        return this
    }

    fun setCanceable(canceable: Boolean): DialogUtils {
        isCanceable = canceable
        builder!!.setCancelable(false)
        return this
    }

    fun dismiss(): DialogUtils {
        dialog!!.dismiss()
        return this
    }

    companion object {
        private val TAG = DialogUtils::class.java.name
    }
}