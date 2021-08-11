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
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.res.ResourcesCompat
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.helper.builder.model.LocomotifAttribute
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*

import android.widget.RadioButton
import id.thork.app.base.BaseParam
import id.thork.app.helper.builder.widget.*
import id.thork.app.helper.builder.widget.view.LocomotifLovBox
import id.thork.app.helper.builder.widget.view.LocomotifRadio


class LocomotifBuilder<T> constructor(val item: T, val context: Context) {
    private val TAG = LocomotifBuilder::class.java.name
    private val LOCOMOTIF_GROUP = "GP"
    private val LOCOMOTIF = "LOCOMOTIF"
    private val LOCOMOTIF_DATEFORMAT = "dd/MM/yyyy"
    private val locomotifDateFormat = SimpleDateFormat(LOCOMOTIF_DATEFORMAT, Locale.UK)

    private val TITLE_SIZE = 18F

    val scrollLayout = ScrollView(context)
    val formLayout = LinearLayout(context)
    var extensionView = View(context)

    var fieldValueMap = hashMapOf<String, Any>()
    var fieldTypeMap = hashMapOf<String, String>()
    var fieldRequired = hashMapOf<String, Boolean>()
    var fieldItems = hashMapOf<String, List<LocomotifAttribute>>()
    var fields: Array<String> = arrayOf()
    var fieldsCaption: Array<String> = arrayOf()

    private var currentField: String? = null

    val locomotifWidget = LocomotifWidget(context)
    lateinit var listener: OnValueChangeListener

    fun forField(fieldName: String): LocomotifBuilder<T> {
        this.currentField = fieldName
        return this
    }

    fun items(items: List<LocomotifAttribute>): LocomotifBuilder<T> {
        currentField?.let { fieldItems.put(it, items) }
        return this
    }

    fun isRequired(required: Boolean): LocomotifBuilder<T> {
        currentField?.let { fieldRequired.put(it, required) }
        return this
    }

    fun setReadOnly():LocomotifBuilder<T> {
        currentField?.let {
            val editText = formLayout.findViewWithTag<AppCompatEditText>(LOCOMOTIF.plus(it))
            val typeName = getFieldTypeByFieldName(it)
                if (typeName.equals("java.util.Date")) {
                    editText.setOnClickListener(null)
                } else {
                    editText.inputType = InputType.TYPE_NULL
                    editText.setTextIsSelectable(true);
                }
        }
        return this
    }

    fun setValue(value: String):LocomotifBuilder<T> {
        currentField?.let {fieldValueMap.put(it, value)
            val editText = formLayout.findViewWithTag<AppCompatEditText>(LOCOMOTIF.plus(it))
            val typeName = getFieldTypeByFieldName(it)
            if (!typeName.equals("java.util.Date")) {
                editText.setText(value)
            }
        }
        return this
    }

    fun setValue(value: Int):LocomotifBuilder<T> {
        currentField?.let {fieldValueMap.put(it, value)}
        return this
    }

    fun setValue(value: Boolean):LocomotifBuilder<T> {
        currentField?.let {fieldValueMap.put(it, value)}
        return this
    }

    fun setValue(value: Any):LocomotifBuilder<T> {
        currentField?.let {fieldValueMap.put(it, value)}
        return this
    }

    fun forFieldItems(fieldName: String, items: List<LocomotifAttribute>) {
        fieldItems.put(fieldName, items)
    }

    fun setupFields(fields: Array<String>) {
        this.fields = fields
    }

    fun setupFieldsCaption(fieldsCaption: Array<String>) {
        this.fieldsCaption = fieldsCaption
    }

    fun setupExtensionView(extensionView: View) {
        this.extensionView = extensionView
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
            fieldValueMap.put(field, "")
            val fieldName = item!!::class.java.getDeclaredField(field)

            val isLovField = locomotifValidator.isListOfValues(field) as Boolean
            val isLovWithExtField = locomotifValidator.isListOfValuesExtension(field) as Boolean
            val isRadioButtonField = locomotifValidator.isRadioButton(field) as Boolean
            val isCheckBoxField = locomotifValidator.isCheckBox(field) as Boolean
            val isSpinnerField = locomotifValidator.isSpinner(field) as Boolean

            val type = fieldName.type
            val typeName = type.name

            Log.d(
                TAG,
                "buildField() $field type $type isLovField $isLovField isLovExtField $isLovWithExtField isRadioButton $isRadioButtonField" +
                        " isSpinner $isSpinnerField"
            )
            if (isLovField) {
                view = createLovWidget(context, field, index)
            } else if (isLovWithExtField) {
                view = createLovWidgetWithExtension(context, field, index, extensionView)
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

    private fun createLovWidget(context: Context, fieldName: String, fieldIndex: Int): LinearLayout {
        val lovWidget = LovWidget(item, context)
        lovWidget.setupField(fieldName, fieldsCaption, fieldIndex, fieldValueMap)
        lovWidget.setupFieldsTypeMap(fieldTypeMap)
        lovWidget.listener = listener
        return lovWidget.create()
    }

    /**
     * Lov Builder
     */
    private fun createLovWidgetWithExtension(
        context: Context, fieldName: String, index: Int,
        extension: View
    ): LinearLayout {
        val widgetValue = item?.getPrivateProperty(fieldName)
        Timber.tag(TAG).d("createLovWidget() fieldName: %s widgetValue: %s", fieldName, widgetValue)
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

        val lovBox = locomotifWidget.createLovWidget(fieldName, widgetValue.toString())
        fieldTypeMap.put(fieldName, "lovBox")
        setupLovListener(lovBox, fieldName)

        val lovWrapper = createExtFieldWrapper()
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        lovWrapper.setBackgroundResource(outValue.resourceId)
        lovWrapper.isClickable = true
        lovBox.layoutParams.width = (LocomotifHelper().getScreenWidth(context) * 0.60).toInt()
        lovWrapper.addView(lovBox)
        lovWrapper.addView(extension)

        val fieldWrapper = createFieldWrapper()
        fieldWrapper.tag = LOCOMOTIF_GROUP.plus(LOCOMOTIF).plus(fieldName)
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
        Timber.tag(TAG).d(
            "createRadioButtonWidget() fieldName: %s widgetValue: %s",
            fieldName, widgetValue
        )
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
        fieldTypeMap.put(fieldName, "radioGroup")
        locomotifWidget.createRadioDefaultValue(radioGroup, widgetValue.toString(), items)
        setupRadioGroupListener(radioGroup, fieldName)

//        for (view in radioGroup.children) {
//            if (view is RadioButton) {
//                if (view.text.equals(widgetValue.toString())) {
//                    radioGroup.check(view.id)
//                }
//            }
//        }

        val lovWrapper = LinearLayout(context)
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        lovWrapper.setBackgroundResource(outValue.resourceId)
        lovWrapper.isClickable = true
        lovWrapper.addView(radioGroup)

        val fieldWrapper = createFieldWrapper()
        fieldWrapper.tag = LOCOMOTIF_GROUP.plus(LOCOMOTIF).plus(fieldName)
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
        fieldTypeMap.put(fieldName, "spinner")
        setupSpinnerListener(spinnerWidget, fieldName)

        val fieldWrapper = createFieldWrapper()
        fieldWrapper.tag = LOCOMOTIF_GROUP.plus(LOCOMOTIF).plus(fieldName)
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
        fieldTypeMap.put(fieldName, "checkBox")
        setupCheckBoxListener(editText, fieldName)
        editText.isChecked = widgetValue.toString().toBoolean()

        val fieldWrapper = createFieldWrapper()
        fieldWrapper.tag = LOCOMOTIF_GROUP.plus(LOCOMOTIF).plus(fieldName)
        fieldWrapper.addView(title)
        fieldWrapper.addView(separator)
        fieldWrapper.addView(editText)
        return fieldWrapper
    }

    /**
     * Text Widget
     */
    private fun createTextWidget(context: Context, fieldName: String, fieldIndex: Int): LinearLayout {
        val textWidget = TextWidget(item, context)
        textWidget.setupField(fieldName, fieldsCaption, fieldIndex, fieldValueMap)
        textWidget.setupFieldsTypeMap(fieldTypeMap)
        textWidget.listener = listener
        return textWidget.create()
    }

    /**
     * Number Widget
     */
    private fun createNumberWidget(context: Context, fieldName: String, fieldIndex: Int): LinearLayout {
        val numberWidget = NumberWidget(item, context)
        numberWidget.setupField(fieldName, fieldsCaption, fieldIndex, fieldValueMap)
        numberWidget.setupFieldsTypeMap(fieldTypeMap)
        numberWidget.listener = listener
        return numberWidget.create()
    }

    private fun createDateWidget(context: Context, fieldName: String, fieldIndex: Int): LinearLayout {
        val dateWidget = DateWidget(item, context)
        dateWidget.setupField(fieldName, fieldsCaption, fieldIndex, fieldValueMap)
        dateWidget.setupFieldsTypeMap(fieldTypeMap)
        dateWidget.listener = listener
        return dateWidget.create()
    }

    private fun setupLovListener(lovBox: LocomotifLovBox, fieldName: String) {
        lovBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                fieldValueMap.put(fieldName, s)

                val typeName = getFieldTypeByFieldName(fieldName)
                if (typeName.equals("java.util.Date")) {
                    item?.setAndReturnPrivateProperty(
                        fieldName,
                        LocomotifHelper().getAppDateFormat(s.toString())
                    )
                    listener.onValueChange(fieldName, s.toString())
                } else {
                    item?.setAndReturnPrivateProperty(fieldName, s.toString())
                    listener.onValueChange(fieldName, s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupRadioGroupListener(radioGroup: LocomotifRadio, fieldName: String) {
        radioGroup.setOnCheckedChangeListener({ radioGroup, checkedId ->
            if (checkedId != BaseParam.APP_EMPTY_ID) {
                val checkedRadioButton: RadioButton = radioGroup.findViewById(checkedId)
                fieldValueMap.put(fieldName, checkedRadioButton.text.toString())
                item?.setAndReturnPrivateProperty(fieldName, checkedRadioButton.text.toString())
                listener.onValueChange(fieldName, checkedRadioButton.text.toString())
            }
        })
    }

    private fun setupCheckBoxListener(checkBox: AppCompatCheckBox, fieldName: String) {
        checkBox.setOnCheckedChangeListener { compoundButton, checked ->
            fieldValueMap.put(fieldName, checked)
            item?.setAndReturnPrivateProperty(fieldName, checked)
            listener.onValueChange(fieldName, checked.toString())
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
                listener.onValueChange(fieldName, selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                fieldValueMap.put(fieldName, "")
                listener.onValueChange(fieldName, "")
            }
        }
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
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
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
        params.setMargins(0, 0, 0, getRealDp(10))
        fieldWrapper.setLayoutParams(params)
        return fieldWrapper
    }

    private fun createExtFieldWrapper(): LinearLayout {
        val fieldWrapper = LinearLayout(context)
        fieldWrapper.orientation = LinearLayout.HORIZONTAL

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, getRealDp(10))
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

    fun setFieldsReadOnly(readOnly: Boolean) {
        //TODO
        //All widget readonly
        fields.forEach {  field ->
            val editText = formLayout.findViewWithTag<AppCompatEditText>(LOCOMOTIF.plus(field))

        }
    }

    fun setWidgetValueByTag(tag: String, value: String) {
        val editText = formLayout.findViewWithTag<AppCompatEditText>(LOCOMOTIF.plus(tag))
        editText.setText(value)
        fieldValueMap.put(tag, value)
    }

    fun getWidgetByTag(tag: String): View {
        val widget = formLayout.findViewWithTag<View>(LOCOMOTIF.plus(tag))
        return widget
    }

    fun getWidgetGroupByTag(tag: String): View {
        val widget = formLayout.findViewWithTag<View>(LOCOMOTIF_GROUP.plus(LOCOMOTIF).plus(tag))
        return widget
    }

    fun getData(): HashMap<String, Any> {
        return fieldValueMap
    }

    fun validate(): Boolean {
        var fieldsEmptyValidator: MutableList<String> = mutableListOf()
        fieldValueMap.forEach { (field, value) ->
            Timber.tag(TAG).d("validate() field: %s value: %s", field, value)
            if (!(value != null && !value.toString().isEmpty() && !value.toString()
                    .equals("null"))
            ) {
                val isRequired = fieldRequired.get(field)
                if (isRequired == true) {
                    fieldsEmptyValidator.add(field)
                }
            }
        }

        Timber.tag(TAG).d("validate() fieldsEmptyValidator: %s", fieldsEmptyValidator.size)
        if (fieldsEmptyValidator.size > 0) {
            fieldsEmptyValidator.forEach { field ->
                val textWidget = getWidgetByTag(field) as AppCompatEditText
                var fieldValidator = StringUtils.capitalize(
                    StringUtils.join(
                        StringUtils.splitByCharacterTypeCamelCase(field), ' '
                    )
                )
                Timber.tag(TAG).d(
                    "validate() fieldsEmptyValidator: %s field: %s",
                    fieldsEmptyValidator.size, fieldValidator
                )
                textWidget.setError("$fieldValidator can't empty")

                Toast.makeText(context, R.string.general_required_fields, Toast.LENGTH_SHORT).show()
            }
            return false
        }
        return true
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