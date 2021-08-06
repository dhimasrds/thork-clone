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
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import okhttp3.internal.toImmutableMap
import org.apache.commons.lang3.StringUtils
import org.json.JSONObject
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatSpinner


class LocomotifBuilder<T> constructor(val item: T, val context: Context) {
    private val TAG = LocomotifBuilder::class.java.name
    private val LOCOMOTIF = "LOCOMOTIF"
    private val LOCOMOTIF_DATEFORMAT = "dd/MM/yyyy"
    private val locomotifDateFormat = SimpleDateFormat(LOCOMOTIF_DATEFORMAT, Locale.UK)

    private val TITLE_SIZE = 21F

    val scrollLayout = ScrollView(context)
    val formLayout = LinearLayout(context)
    var fieldValueMap = hashMapOf<String, Any>()
    var fieldItems = hashMapOf<String, List<LocomotifAttribute>>()
    var fields: Array<String> = arrayOf()
    var fieldsCaption: Array<String> = arrayOf()

    val locomotifWidget = LocomotifWidget(context)

    fun forFieldItems(fieldName: String, items: List<LocomotifAttribute>) {
        fieldItems.put(fieldName, items)
    }

    fun setupFields(fields: Array<String>) {
        this.fields = fields
    }

    fun setupFieldsCaption(fieldsCaption: Array<String>) {
        this.fieldsCaption = fieldsCaption
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
    fun build(): ScrollView {
        formLayout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(getRealDp(10), getRealDp(10), getRealDp(10), getRealDp(10))
        formLayout.setLayoutParams(params)

        val locomotifValidator = LocomotifValidator(item)
        var view: View? = null
        fields.forEachIndexed { index, field ->
            val fieldName = item!!::class.java.getDeclaredField(field)

            val isLovField = locomotifValidator.isListOfValues(field) as Boolean
            val isRadioButtonField = locomotifValidator.isRadioButton(field) as Boolean
            val isCheckBoxField = locomotifValidator.isCheckBox(field) as Boolean
            val isSpinnerField = locomotifValidator.isSpinner(field) as Boolean

            val type = fieldName.type
            val typeName = type.name

            Log.d(
                TAG,
                "buildField() $field type $type isLovField $isLovField isRadioButton $isRadioButtonField" +
                        " isSpinner $isSpinnerField"
            )
            if (isLovField) {
                view = createLovWidget(context, field, index)
            } else if (isRadioButtonField) {
                view =
                    fieldItems.get(field)?.let {
                        createRadioButtonWidget(
                            context, field,
                            it, index
                        )
                    }
            } else if (isCheckBoxField) {
                view = createCheckBoxWidget(context, field, index)
            } else if (isSpinnerField) {
                view =
                    fieldItems.get(field)?.let {
                        createSpinnerWidget(
                            context, field,
                            it, index
                        )
                    }
            } else if (typeName.equals("java.lang.String")) {
                view = createTextWidget(context, field, index)
            } else if (typeName.equals("java.lang.Integer")) {
                view = createNumberWidget(context, field, index)
            } else if (typeName.equals("java.util.Date")) {
                view = createDateWidget(context, field, index)
            }
            formLayout.addView(view)
        }

        scrollLayout.addView(formLayout)
        val paramsScroll = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        scrollLayout.setLayoutParams(paramsScroll)
        return scrollLayout
    }

    /**
     * Lov Builder
     */
    private fun createLovWidget(context: Context, fieldName: String, index: Int): LinearLayout {
        val widgetValue = item?.getPrivateProperty(fieldName)
        widgetValue.whatIfNotNull {
            fieldValueMap.put(fieldName, it)
        }

        val title = createFieldLabel(fieldName, index)

        val separator = View(context)
        separator.setBackgroundColor(Color.LTGRAY)
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            getRealDp(1)
        )
        setMargins(separator, 0, 0, 0, getRealDp(0))

        val editText = locomotifWidget.createLovWidget(fieldName, widgetValue.toString())
        setupEditTextListener(editText, fieldName)

        val lovWrapper = LinearLayout(context)
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        lovWrapper.setBackgroundResource(outValue.resourceId)
        lovWrapper.isClickable = true
        lovWrapper.addView(editText)

        val fieldWrapper = createFieldWrapper()
        fieldWrapper.addView(title)
        fieldWrapper.addView(separator)
        fieldWrapper.addView(lovWrapper)
        return fieldWrapper
    }

    /**
     * RadioButton Builder
     */
    private fun createRadioButtonWidget(
        context: Context,
        fieldName: String,
        items: List<LocomotifAttribute>,
        index: Int
    ): LinearLayout {
        val widgetValue = item?.getPrivateProperty(fieldName)
        widgetValue.whatIfNotNull {
            fieldValueMap.put(fieldName, it)
        }

        val title = createFieldLabel(fieldName, index)

        val separator = View(context)
        separator.setBackgroundColor(Color.LTGRAY)
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            getRealDp(1)
        )
        setMargins(separator, 0, 0, 0, getRealDp(0))

        val radioGroup =
            locomotifWidget.createRadioButtonWidget(fieldName, widgetValue.toString(), items)
        setupRadioGroupListener(radioGroup, fieldName)

        val lovWrapper = LinearLayout(context)
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        lovWrapper.setBackgroundResource(outValue.resourceId)
        lovWrapper.isClickable = true
        lovWrapper.addView(radioGroup)

        val fieldWrapper = createFieldWrapper()
        fieldWrapper.addView(title)
        fieldWrapper.addView(separator)
        fieldWrapper.addView(lovWrapper)
        return fieldWrapper
    }


    /**
     * Spinner Widget
     */
    private fun createSpinnerWidget(
        context: Context,
        fieldName: String,
        items: List<LocomotifAttribute>,
        index: Int
    ): LinearLayout {
        val widgetValue = item?.getPrivateProperty(fieldName)
        widgetValue.whatIfNotNull {
            fieldValueMap.put(fieldName, it)
        }

        val title = createFieldLabel(fieldName, index)

        val separator = View(context)
        separator.setBackgroundColor(Color.LTGRAY)
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            getRealDp(1)
        )
        setMargins(separator, 0, 0, 0, getRealDp(0))

        val spinnerWidget =
            locomotifWidget.createSpinnerWidget(fieldName, widgetValue.toString(), items)
        setupSpinnerListener(spinnerWidget, fieldName)

        val fieldWrapper = createFieldWrapper()
        fieldWrapper.addView(title)
        fieldWrapper.addView(separator)
        fieldWrapper.addView(spinnerWidget)
        return fieldWrapper
    }

    /**
     * CheckBox Widget
     */
    private fun createCheckBoxWidget(
        context: Context,
        fieldName: String,
        index: Int
    ): LinearLayout {
        val widgetValue = item?.getPrivateProperty(fieldName)
        widgetValue.whatIfNotNull {
            fieldValueMap.put(fieldName, it)
        }

        val title = createFieldLabel(fieldName, index)

        val separator = View(context)
        separator.setBackgroundColor(Color.LTGRAY)
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            getRealDp(1)
        )
        setMargins(separator, 0, 0, 0, getRealDp(0))

        val editText = locomotifWidget.createCheckBoxWidget(fieldName, widgetValue.toString())
        setupCheckBoxListener(editText, fieldName)

        val fieldWrapper = createFieldWrapper()
        fieldWrapper.addView(title)
        fieldWrapper.addView(separator)
        fieldWrapper.addView(editText)
        return fieldWrapper
    }

    /**
     * Text Widget
     */
    private fun createTextWidget(context: Context, fieldName: String, index: Int): LinearLayout {
        val widgetValue = item?.getPrivateProperty(fieldName)
        widgetValue.whatIfNotNull {
            fieldValueMap.put(fieldName, it)
        }

        val title = createFieldLabel(fieldName, index)

        val separator = View(context)
        separator.setBackgroundColor(Color.LTGRAY)
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            getRealDp(1)
        )
        setMargins(separator, 0, 0, 0, getRealDp(0))

        val editText = locomotifWidget.createTextWidget(fieldName, widgetValue.toString())
        setupEditTextListener(editText, fieldName)

        val fieldWrapper = createFieldWrapper()
        fieldWrapper.addView(title)
        fieldWrapper.addView(separator)
        fieldWrapper.addView(editText)
        return fieldWrapper
    }

    private fun createNumberWidget(context: Context, fieldName: String, index: Int): LinearLayout {
        val widgetValue = item?.getPrivateProperty(fieldName)
        widgetValue.whatIfNotNull {
            fieldValueMap.put(fieldName, it)
        }

        val title = createFieldLabel(fieldName, index)

        val separator = View(context)
        separator.setBackgroundColor(Color.LTGRAY)
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            getRealDp(1)
        )
        setMargins(separator, 0, 0, 0, getRealDp(0))

        val editText = locomotifWidget.createNumberWidget(fieldName, widgetValue.toString())
        setupEditTextListener(editText, fieldName)

        val fieldWrapper = createFieldWrapper()
        fieldWrapper.addView(title)
        fieldWrapper.addView(separator)
        fieldWrapper.addView(editText)
        return fieldWrapper
    }

    private fun createDateWidget(context: Context, fieldName: String, index: Int): LinearLayout {
        val widgetValue = item?.getPrivateProperty(fieldName)
        var currentDate = ""
        widgetValue.whatIfNotNull {
            fieldValueMap.put(fieldName, it)
        }
        widgetValue.whatIfNotNull(
            whatIf = {
                currentDate = locomotifDateFormat.format(it)
                fieldValueMap.put(fieldName, currentDate)
            },
            whatIfNot = {
                currentDate = locomotifDateFormat.format(Calendar.getInstance().getTime())
                fieldValueMap.put(fieldName, currentDate)
            }
        )

        val title = createFieldLabel(fieldName, index)

        val separator = View(context)
        separator.setBackgroundColor(Color.LTGRAY)
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            getRealDp(1)
        )
        setMargins(separator, 0, 0, 0, getRealDp(0))

        val editText = locomotifWidget.createDateWidget(fieldName, currentDate)
        setupEditTextListener(editText, fieldName)
        setupDateWidgetListener(editText)

        val fieldWrapper = createFieldWrapper()
        fieldWrapper.addView(title)
        fieldWrapper.addView(separator)
        fieldWrapper.addView(editText)
        return fieldWrapper
    }

    private fun setupEditTextListener(editText: EditText, fieldName: String) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                fieldValueMap.put(fieldName, s)

                val typeName = getFieldTypeByFieldName(fieldName)
                if (typeName.equals("java.util.Date")) {
                    item?.setAndReturnPrivateProperty(
                        fieldName,
                        LocomotifHelper().getAppDateFormat(s.toString())
                    )
                } else {
                    item?.setAndReturnPrivateProperty(fieldName, s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupRadioGroupListener(radioGroup: RadioGroup, fieldName: String) {
        radioGroup.setOnCheckedChangeListener({ radioGroup, checkedId ->
            val checkedRadioButton: RadioButton = radioGroup.findViewById(checkedId)
            fieldValueMap.put(fieldName, checkedRadioButton.text.toString())
            item?.setAndReturnPrivateProperty(fieldName, checkedRadioButton.text.toString())
        })
    }

    private fun setupCheckBoxListener(checkBox: AppCompatCheckBox, fieldName: String) {
        checkBox.setOnCheckedChangeListener { compoundButton, checked ->
            fieldValueMap.put(fieldName, checked)
            item?.setAndReturnPrivateProperty(fieldName, checked)
        }
    }

    private fun setupSpinnerListener(spinner: AppCompatSpinner, fieldName: String) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                fieldValueMap.put(fieldName, selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                fieldValueMap.put(fieldName, "")
            }
        }
    }

    private fun setupDateWidgetListener(editText: EditText) {
        val calendar = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(editText, calendar)
        }

        editText.setOnClickListener {
            DatePickerDialog(
                context, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateLabel(editText: EditText, calendar: Calendar) {
        editText.setText(locomotifDateFormat.format(calendar.getTime()))
    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = view.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    private fun createFieldLabel(fieldName: String, index: Int): TextView {
        val title = TextView(context)
        title.apply {
            setTextColor(Color.BLACK)
            val typeface = ResourcesCompat.getFont(context, R.font.roboto)
            setTypeface(typeface, Typeface.BOLD)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, TITLE_SIZE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )

            fieldsCaption.whatIfNotNullOrEmpty(
                whatIf = {
                    text = it[index]
                },
                whatIfNot = {
                    text = StringUtils.capitalize(
                        StringUtils.join(
                            StringUtils.splitByCharacterTypeCamelCase(fieldName), ' '
                        )
                    );
                }
            )
        }
        return title
    }

    private fun createFieldWrapper(): LinearLayout {
        val fieldWrapper = LinearLayout(context)
        fieldWrapper.orientation = LinearLayout.VERTICAL

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, getRealDp(10), 0, 0)
        fieldWrapper.setLayoutParams(params)
        return fieldWrapper
    }

    @ColorInt
    private fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

    private fun getRealDp(dp: Int): Int {
        val scale = context.getResources().getDisplayMetrics().density;
        val realDp: Int = (dp * scale + 0.5f).toInt()
        return realDp
    }

    private fun getFieldTypeByFieldName(fieldName: String): String {
        val field = item!!::class.java.getDeclaredField(fieldName)
        val type = field.type
        val typeName = type.name
        return typeName
    }

    fun setFieldReadOnlyByTag(tag: String) {
        val editText = formLayout.findViewWithTag<AppCompatEditText>(LOCOMOTIF.plus(tag))
        val typeName = getFieldTypeByFieldName(tag)
        if (typeName.equals("java.util.Date")) {
            editText.setOnClickListener(null)
        } else {
            editText.inputType = InputType.TYPE_NULL
            editText.setTextIsSelectable(true);
        }
    }

    fun setWidgetValueByTag(tag: String, value: String) {
        val editText = formLayout.findViewWithTag<AppCompatEditText>(LOCOMOTIF.plus(tag))
        editText.setText(value)
        fieldValueMap.put(tag, value)
    }

    fun getWidgetByTag(tag: String): View {
        val editText = formLayout.findViewWithTag<AppCompatEditText>(LOCOMOTIF.plus(tag))
        return editText
    }

    fun getData(): HashMap<String, Any> {
        return fieldValueMap
    }

    /**
     * Convert to JSON have bugs issue after update field
     */
//    fun getDataAsJson(): String {
//        val rootJson = JSONObject(fieldValueMap.toImmutableMap()).toString()
//        return rootJson
//    }

    fun getDataAsEntity(): T {
        return item
    }
}