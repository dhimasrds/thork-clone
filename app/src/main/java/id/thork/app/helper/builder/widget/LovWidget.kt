package id.thork.app.helper.builder.widget

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.helper.builder.LocomotifHelper
import id.thork.app.helper.builder.widget.core.LocomotifLovBox
import timber.log.Timber

class LovWidget<T> (entity: T, context: Context) : AbstractWidget<T> (entity, context) {
    private val TAG = LovWidget::class.java.name

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

            val lovBox = createLovWidget(fieldName, widgetValue.toString())
            fieldTypeMap.put(fieldName, "lovBox")
            setupLovListener(lovBox, fieldName)

            val lovWrapper = LinearLayout(context)
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            lovWrapper.setBackgroundResource(outValue.resourceId)
            lovWrapper.isClickable = true
            lovWrapper.orientation = LinearLayout.HORIZONTAL
            lovWrapper.addView(lovBox)

            val fieldWrapper = createFieldWrapper(title, separator, lovWrapper)
            return fieldWrapper
        }
        return LinearLayout(context)
    }

    /**
     * Lov only edittext as component
     */
    private fun createLovWidget(fieldName: String, widgetValue: String): LocomotifLovBox {
        Timber.tag(TAG).d("createLovWidget() fieldName: %s widgetValue: %s", fieldName, widgetValue)
        val editText = LocomotifLovBox(context)
        editText.apply {
            tag = LOCOMOTIF.plus(fieldName)
            hint = WIDGET_HINT_SELECT.plus(fieldName)
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
            setBackgroundResource(outValue.resourceId)
            isFocusableInTouchMode = false
            isClickable = true
            inputType = InputType.TYPE_NULL
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.BLACK)
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
            setTypeface(typeface)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, VALUE_SIZE)
            setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            //Disable icon
//            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_loco_search)
//            drawable.whatIfNotNull {
//                DrawableCompat.setTint(
//                    DrawableCompat.wrap(it),
//                    ContextCompat.getColor(context, R.color.blue)
//                )
//            }
//            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
            setText(LocomotifHelper().convertNullToEmpty(widgetValue))
        }
        return editText
    }

    private fun setupLovListener(lovBox: LocomotifLovBox, fieldName: String) {
        lovBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                fieldValueMap.put(fieldName, s)

                val typeName = getFieldTypeByFieldName(fieldName)
                if (typeName.equals("java.util.Date")) {
                    entity?.setAndReturnPrivateProperty(
                        fieldName,
                        LocomotifHelper().getAppDateFormat(s.toString())
                    )
                    listener.onValueChange(fieldName, s.toString())
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