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

package id.thork.app.helper.builder

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import java.lang.reflect.Method
import java.util.*


class FormBuilder<T> constructor(val item: T, val context: Context) {
    private val TAG = FormBuilder::class.java.name

    val formLayout = LinearLayout(context)
    var fieldValueMap = hashMapOf<String, Any>()
    var fields: Array<String> = arrayOf()

    fun setupFields(fields: Array<String>) {
        this.fields = fields
    }

    fun <T : Any> T.getPrivateProperty(variableName: String): Any? {
        return javaClass.getDeclaredField(variableName).let { field ->
            field.isAccessible = true
            return@let field.get(this)
        }
    }

    fun <T : Any> T.setAndReturnPrivateProperty(variableName: String, data: Any): Any? {
        return javaClass.getDeclaredField(variableName).let { field ->
            field.isAccessible = true
            field.set(this, data)
            return@let field.get(this)
        }
    }

    private fun getMethodNames(methods: Array<Method>): MutableList<String> {
        val methodNames: MutableList<String> = mutableListOf()
        for (method in methods) {
            Log.d(TAG, "method ${method.name}")
            methodNames.add(method.name)
        }
        return methodNames
    }

    @SuppressLint("LogNotTimber")
    fun build(): LinearLayout {
        formLayout.setOrientation(LinearLayout.VERTICAL);
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(getRealDp(10), getRealDp(10), getRealDp(10), getRealDp(10))
        formLayout.setLayoutParams(params)

        var view: View? = null
        for (field in fields) {
            val fieldName = item!!::class.java.getDeclaredField(field)
            val type = fieldName.type
            val typeName = type.name

            Log.d(TAG, "field $field type $type")
            if (typeName.equals("java.lang.String")) {
                view = createTextWidget(context, field)
            } else if (typeName.equals("java.lang.Integer")) {
                view = createNumberWidget(context, field)
            } else if (typeName.equals("java.util.Date")) {
                view = createDateWidget(context, field)
            }
            formLayout.addView(view)
        }
        return formLayout
    }

    fun createTextWidget(context: Context, fieldName: String): LinearLayout {
        val widgetValue = item?.getPrivateProperty(fieldName)
        widgetValue.whatIfNotNull {
            fieldValueMap.put(fieldName, it)
        }

        val fieldWrapper = LinearLayout(context)
        fieldWrapper.orientation = LinearLayout.VERTICAL

        val title = TextView(context)
        title.apply {
            setTextColor(Color.BLACK)
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
            setTypeface(typeface, Typeface.BOLD)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 26F)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            setText(fieldName)
        }

        val separator = View(context)
        separator.setBackgroundColor(Color.GRAY)
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1
        )
        setMargins(separator, 0, 0, 0, getRealDp(5))

        val editText = AppCompatEditText(context)
        editText.apply {
            tag = fieldName
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.BLACK)
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
            setTypeface(typeface)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
            setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            setText(widgetValue.toString())
        }
        setupEditTextListener(editText, fieldName)

        fieldWrapper.addView(title)
        fieldWrapper.addView(separator)
        fieldWrapper.addView(editText)

//        val editText2 = AppCompatEditText(context)
//        editText2.setText("HALOOHA")
//        fieldWrapper.addView(editText2)
        fieldWrapper.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        return fieldWrapper
    }

    fun createNumberWidget(context: Context, fieldName: String): EditText {
        val widgetValue = item?.getPrivateProperty(fieldName)
        widgetValue.whatIfNotNull {
            fieldValueMap.put(fieldName, it)
        }

        val editText = AppCompatEditText(context)
        editText.tag = fieldName
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        editText.setText(widgetValue.toString())
        setupEditTextListener(editText, fieldName)
        return editText
    }

    fun createDateWidget(context: Context, fieldName: String): EditText {
        val widgetValue = item?.getPrivateProperty(fieldName)
        widgetValue.whatIfNotNull {
            fieldValueMap.put(fieldName, it)
        }

        val editText = AppCompatEditText(context)
        editText.tag = fieldName
        editText.inputType = InputType.TYPE_CLASS_DATETIME
        editText.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        editText.setText(widgetValue.toString())
        setupEditTextListener(editText, fieldName)
        return editText
    }

    private fun setupEditTextListener(editText: EditText, fieldName: String) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                fieldValueMap.put(fieldName, s)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    private fun getRealDp(dp: Int):Int {
        val scale = context.getResources().getDisplayMetrics().density;
        val realDp: Int = (dp * scale + 0.5f).toInt()
        return realDp
    }

    fun getData(): HashMap<String, Any> {
        return fieldValueMap
    }
}