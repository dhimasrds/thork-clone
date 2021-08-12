package id.thork.app.helper.builder.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

abstract class AbstractWidget<T>(val entity: T, val context: Context) {
    /**
     * TAG
     */
    val LOCOMOTIF = "LOCOMOTIF"
    val LOCOMOTIF_GROUP = "GP"

    /**
     * Widget Hint
     */
    val WIDGET_HINT_SELECT = "Select "
    val WIDGET_HINT_TYPE = "Type "

    /**
     * Widget Dimens
     */
    val TITLE_SIZE = 18F
    val VALUE_SIZE = 16F

    /**
     * Date Format
     */
    val LOCOMOTIF_DATEFORMAT = "dd/MM/yyyy"
    val locomotifDateFormat = SimpleDateFormat(LOCOMOTIF_DATEFORMAT, Locale.UK)


    var fieldIndex: Int = 0
    var fieldName: String? = ""
    var fieldsCaption: Array<String> = arrayOf()
    var fieldValueMap: HashMap<String, Any> = hashMapOf()
    var fieldTypeMap: HashMap<String, String> = hashMapOf()

    abstract fun create(): LinearLayout

    fun setupField(fieldName: String, fieldsCaption: Array<String>, fieldIndex: Int,
                   fieldValueMap: HashMap<String, Any>) {
        this.fieldName = fieldName
        this.fieldsCaption = fieldsCaption
        this.fieldIndex = fieldIndex
        this.fieldValueMap = fieldValueMap
    }

    fun setupFieldsTypeMap( fieldTypeMap: HashMap<String, String>) {
        this.fieldTypeMap = fieldTypeMap
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

    fun getFieldTypeByFieldName(fieldName: String): String {
        val field = entity!!::class.java.getDeclaredField(fieldName)
        val type = field.type
        val typeName = type.name
        return typeName
    }

    fun createFieldLabel(fieldName: String, index: Int): TextView {
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

    fun createSeparator(): View {
        val separator = View(context)
        separator.setBackgroundColor(Color.LTGRAY)
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            getRealDp(1)
        )
        return separator
    }

    fun createFieldWrapper(vararg views: View): LinearLayout {
        val fieldWrapper = LinearLayout(context)
        fieldWrapper.orientation = LinearLayout.VERTICAL

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, getRealDp(10))
        fieldWrapper.setLayoutParams(params)

        fieldWrapper.tag = LOCOMOTIF_GROUP.plus(LOCOMOTIF).plus(fieldName)
        views.forEach { view ->
            fieldWrapper.addView(view)
        }
        return fieldWrapper
    }

    fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = view.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    fun getRealDp(dp: Int): Int {
        val scale = context.getResources().getDisplayMetrics().density;
        val realDp: Int = (dp * scale + 0.5f).toInt()
        return realDp
    }

}