package id.thork.app.helper.builder.widget

import android.app.DatePickerDialog
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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.helper.builder.LocomotifHelper
import timber.log.Timber
import java.util.*

class DateWidget<T> (entity: T, context: Context) : AbstractWidget<T> (entity, context) {
    private val TAG = DateWidget::class.java.name

    lateinit var listener: OnValueChangeListener

    override fun create(): LinearLayout {
        fieldName?.let { fieldName ->
            val widgetValue = entity?.getPrivateProperty(fieldName)
            var currentDate = ""

            Timber.tag(TAG).d("createWidget() fieldName: %s widgetValue: %s", fieldName, widgetValue)
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
            val title = createFieldLabel(fieldName, fieldIndex)
            val separator = createSeparator()
            setMargins(separator, 0, 0, 0, getRealDp(0))

            val editText = createDateWidget(fieldName, currentDate)
            fieldTypeMap.put(fieldName, "editText")
            setupEditTextListener(editText, fieldName)
            editText.setText(currentDate)
            setupDateWidgetListener(editText)

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
     * EditText as Component
     */
    fun createDateWidget(fieldName: String, currentDate: String): AppCompatEditText {
        val editText = AppCompatEditText(context)
        editText.apply {
            tag = LOCOMOTIF.plus(fieldName)
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.BLACK)
            inputType = InputType.TYPE_NULL
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
            setTypeface(typeface)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, VALUE_SIZE)
            setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_date)
            drawable.whatIfNotNull {
                DrawableCompat.setTint(
                    DrawableCompat.wrap(it),
                    ContextCompat.getColor(context, R.color.black)
                )
            }
            setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            setText(currentDate)
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
}