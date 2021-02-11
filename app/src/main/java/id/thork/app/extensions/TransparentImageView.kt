package id.thork.app.extensions

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatImageView

/**
 * Created by M.Reza Sulaiman on 11/02/21
 * Jakarta, Indonesia.
 */
class TransparantImageView : AppCompatImageView {
    private var mScrollView: ScrollView? = null

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    fun setRootView(scrollView: ScrollView?) {
        mScrollView = scrollView
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        val action = event.action
        return when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Disallow ScrollView to intercept touch events.
                mScrollView!!.requestDisallowInterceptTouchEvent(true)
                // Disable touch on transparent view
                false
            }
            MotionEvent.ACTION_UP -> {
                // Allow ScrollView to intercept touch events.
                mScrollView!!.requestDisallowInterceptTouchEvent(false)
                true
            }
            else -> true
        }
    }
}