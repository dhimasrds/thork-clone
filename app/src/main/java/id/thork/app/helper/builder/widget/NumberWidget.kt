package id.thork.app.helper.builder.widget

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.helper.builder.LocomotifHelper
import timber.log.Timber

class NumberWidget<T> (entity: T, context: Context) : AbstractWidget<T> (entity, context) {
    private val TAG = NumberWidget::class.java.name

    lateinit var listener: OnValueChangeListener

    override fun create(): LinearLayout {
        fieldName?.let { fieldName ->
            val widgetValue = entity?.getPrivateProperty(fieldName)
            Timber.tag(TAG).d("createWidget() fieldName: %s widgetValue: %s", fieldName, widgetValue)
            widgetValue.whatIfNotNull {
                fieldValueMap.put(fieldName, it)
            }
            val title = createFieldLabel(fieldName, fieldIndex)
            val separator = createSeparator()
            setMargins(separator, 0, 0, 0, getRealDp(0))

            val editText = createNumberWidget(fieldName, widgetValue.toString())
            fieldTypeMap.put(fieldName, "editText")
            setupEditTextListener(editText, fieldName)
            editText.setText(LocomotifHelper().convertNullToZero(widgetValue.toString()))

            val lovWrapper = LinearLayout(context)
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            lovWrapper.setBackgroundResource(outValue.resourceId)
            lovWrapper.isClickable = true
            lovWrapper.orientation = LinearLayout.HORIZONTAL
            lovWrapper.addView(editText)

            val fieldWrapper = createFieldWrapper(title, separator, lovWrapper)
            return fieldWrapper
        }
        return LinearLayout(context)
    }

    /**
     * Edittext as component
     */
    fun createNumberWidget(fieldName: String, widgetValue: String): AppCompatEditText {
        val editText = AppCompatEditText(context)
        editText.apply {
            tag = LOCOMOTIF.plus(fieldName)
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.BLACK)
            inputType = InputType.TYPE_CLASS_NUMBER
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
            setTypeface(typeface)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, VALUE_SIZE)
            setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            setText(LocomotifHelper().convertNullToZero(widgetValue))
        }
        return editText
    }

    private fun setupEditTextListener(editText: EditText, fieldName: String) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                Timber.tag(TAG).d("setupEditTextListener()")
                fieldValueMap.put(fieldName, s)

                val typeName = getFieldTypeByFieldName(fieldName)
                if (typeName.equals("java.util.Date")) {
                    entity?.setAndReturnPrivateProperty(
                        fieldName,
                        LocomotifHelper().getAppDateFormat(s.toString())
                    )
                    listener.onValueChange(fieldName, s.toString())
                } else if (typeName.equals("java.lang.Integer")) {
                    var fieldIntValue: Int = 0
                    if (s.toString().equals("")) {
                        fieldIntValue = 0
                    } else {
                        fieldIntValue = s.toString().toInt()
                    }
                    entity?.setAndReturnPrivateProperty(fieldName, fieldIntValue)
                    listener.onValueChange(fieldName, fieldIntValue.toString())
                } else {
                    entity?.setAndReturnPrivateProperty(fieldName, s.toString())
                    listener.onValueChange(fieldName, s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }
}