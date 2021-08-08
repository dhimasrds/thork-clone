package id.thork.app.helper.builder.widget

import android.content.Context
import android.util.AttributeSet

class LocomotifLovBox : androidx.appcompat.widget.AppCompatEditText {
    var value: String? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)
}