package id.thork.app.helper.builder.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import id.thork.app.helper.builder.LocomotifBuilder
import timber.log.Timber

class LocomotifRadio : RadioGroup {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
}