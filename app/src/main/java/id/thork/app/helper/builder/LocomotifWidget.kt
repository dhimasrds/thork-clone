package id.thork.app.helper.builder

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.InputType
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R

class LocomotifWidget constructor(val context: Context) {
    private val LOCOMOTIF = "LOCOMOTIF"
    private val WIDGET_HINT_SELECT = "Select "
    private val VALUE_SIZE = 14F

    /**
     * Lov Widget
     */
    fun createLovWidget(fieldName: String, widgetValue: String): AppCompatEditText {
        val editText = AppCompatEditText(context)
        editText.apply {
            tag = LOCOMOTIF.plus(fieldName)
            hint = WIDGET_HINT_SELECT.plus(fieldName)
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
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
            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_loco_search)
            drawable.whatIfNotNull {
                DrawableCompat.setTint(
                    DrawableCompat.wrap(it),
                    ContextCompat.getColor(context, R.color.blue)
                )
            }
            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
            setText(widgetValue)
        }
        return editText
    }

    /**
     * RadioButton Widget
     */
    fun createRadioButtonWidget(
        fieldName: String,
        widgetValue: String,
        items: List<LocomotifAttribute>
    ): RadioGroup {
        val radioGroup = RadioGroup(context)
        radioGroup.apply {
            tag = LOCOMOTIF.plus(fieldName)
            orientation = RadioGroup.HORIZONTAL
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)

            isClickable = true
            setBackgroundColor(Color.TRANSPARENT)
            setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_search)
            drawable.whatIfNotNull {
                DrawableCompat.setTint(
                    DrawableCompat.wrap(it),
                    ContextCompat.getColor(context, R.color.black)
                )
            }
        }

        var index = 0
        for (attrib in items) {
            val radioButton = AppCompatRadioButton(context)
            radioButton.text = attrib.value
            radioButton.id = index
            radioButton.buttonTintList = LocomotifHelper().fetchDefaultColorStateList(context)

            radioGroup.addView(radioButton)
            if (widgetValue.equals(attrib.value)) {
                radioButton.isChecked = true
            } else if (index == 0) {
                radioButton.isChecked = true
            }
            index++
        }
        return radioGroup
    }

    /**
     * CheckBox Widget
     */
    fun createCheckBoxWidget(fieldName: String, widgetValue: String): AppCompatCheckBox {
        val checkBox = AppCompatCheckBox(context)
        checkBox.apply {
            tag = LOCOMOTIF.plus(fieldName)
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.BLACK)
            buttonTintList = LocomotifHelper().fetchDefaultColorStateList(context)

            val typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
            setTypeface(typeface)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, VALUE_SIZE)
            setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            isChecked = widgetValue.toBoolean()
        }
        return checkBox
    }

    /**
     * Text Widget
     */
    fun createTextWidget(fieldName: String, widgetValue: String): AppCompatEditText {
        val editText = AppCompatEditText(context)
        editText.apply {
            tag = LOCOMOTIF.plus(fieldName)
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
            setText(widgetValue)
        }
        return editText
    }

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
            setText(widgetValue.toString())
        }
        return editText
    }

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
}