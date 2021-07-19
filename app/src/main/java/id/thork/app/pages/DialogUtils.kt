/*
 * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * This.ID. ("Confidential Information").
 *
 * Such Confidential Information shall not be disclosed and shall
 * use it only	 in accordance with the terms of the license agreement
 * entered into with This.ID; other than in accordance with the written
 * permission of This.ID.
 */
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
    val TAG = DialogUtils::class.java.name

    interface DialogUtilsListener {
        fun onPositiveButton()
        fun onNegativeButton()
    }

    private var context: Context
    private var resource: Int? = null
    private var root: ViewGroup? = null
    private var theme = 0
    private var title = 0
    private var message = 0
    private var listener: DialogUtilsListener? = null
    private var isPositiveButton = false
    private var isNegativeButton = false
    private var isCanceable = false
    private var builder: AlertDialog.Builder
    private lateinit var dialogView: View
    private var inflater: LayoutInflater? = null
    private lateinit var dialog: AlertDialog
    private var useTheme: Boolean = false

    var isCancelable: Boolean = false
    var isVisible: Boolean = false
    var positiveButtonLabel: Int? = null
    var negativeButtonLabel: Int? = null

    constructor(context: Context) {
        this.context = context
        this.useTheme = false
        builder = AlertDialog.Builder(context)
    }

    constructor(context: Context, theme: Int) {
        this.context = context
        this.useTheme = true
        this.theme = theme
        builder = AlertDialog.Builder(context, theme)
    }

    fun create(): DialogUtils {
        if (useTheme) {
            builder = AlertDialog.Builder(context, theme)
        } else {
            builder = AlertDialog.Builder(context)
        }
        if (title != 0) {
            builder.setTitle(title)
        }
        if (message != 0) {
            builder.setMessage(message)
        }
        dialogView = inflater!!.inflate(resource!!, root)
        builder.setView(dialogView)
        builder.setCancelable(isCancelable)
        return this
    }

    fun show() {
        dialog = builder.show()
        isVisible = true
        dialog.setOnCancelListener {
            dialog.dismiss()
            isVisible = false
        }
    }

    fun setRounded(rounded: Boolean): DialogUtils {
        if (rounded) {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return this
    }

    fun setTitles(title: Int): DialogUtils {
        this.title = title
        return this
    }

    fun setViewId(id: Int): View {
        return dialogView.findViewById(id)
    }

    fun setInflater(resource: Int, root: ViewGroup?, layoutInflater: LayoutInflater?): DialogUtils {
        inflater = layoutInflater
        this.resource = resource
        this.root = root
        return this
    }

    fun setMessage(message: Int): DialogUtils {
        this.message = message
        return this
    }

    fun setPositiveButtonLabel(positiveButtonLabel: Int?): DialogUtils {
        this.positiveButtonLabel = positiveButtonLabel
        if (positiveButtonLabel == null) {
            builder.setPositiveButton(null, null)
            isPositiveButton = false
        } else {
            builder.setPositiveButton(
                positiveButtonLabel,
                object : DialogInterface.OnClickListener {
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

    fun setNegativeButtonLabel(negativeButtonLabel: Int?): DialogUtils {
        this.negativeButtonLabel = negativeButtonLabel
        if (negativeButtonLabel == null) {
            builder.setNegativeButton(null, null)
            isNegativeButton = false
        } else {
            builder.setNegativeButton(
                negativeButtonLabel,
                object : DialogInterface.OnClickListener {
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
        return this
    }

    fun dismiss(): DialogUtils {
        dialog.dismiss()
        isVisible = false;
        return this
    }

}