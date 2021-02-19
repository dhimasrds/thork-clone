package id.thork.app.pages.settings_log.element

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.pages.settings_log.LogActivity

/**
 * Created by Raka Putra on 1/19/21
 * Jakarta, Indonesia.
 */
class RecyclerViewDividerItemDecoration(context: Context, vertical: Int, i1: Int) : RecyclerView.ItemDecoration() {
    private val ATTRS = intArrayOf(
        R.attr.listDivider
    )

    val HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL

    val VERTICAL_LIST = LinearLayoutManager.VERTICAL

    private lateinit var mDivider: Drawable
    private var mOrientation = 0
    private lateinit var context: Context
    private var margin = 0

    fun RecyclerViewDividerItemDecoration(context: Context, orientation: Int, margin: Int) {
        this.context = context
        this.margin = margin
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)!!
        a.recycle()
        setOrientation(orientation)
    }

    fun setOrientation(orientation: Int) {
        require(!(orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST)) { "invalid orientation" }
        mOrientation = orientation
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    fun drawVertical(c: Canvas?, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider!!.intrinsicHeight
            mDivider!!.setBounds(left + dpToPx(margin), top, right - dpToPx(margin), bottom)
            mDivider!!.draw(c!!)
        }
    }

    fun drawHorizontal(c: Canvas?, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + mDivider!!.intrinsicHeight
            mDivider!!.setBounds(left, top + dpToPx(margin), right, bottom - dpToPx(margin))
            mDivider!!.draw(c!!)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (mOrientation == VERTICAL_LIST) {
            outRect[0, 0, 0] = mDivider!!.intrinsicHeight
        } else {
            outRect[0, 0, mDivider!!.intrinsicWidth] = 0
        }
    }

    private fun dpToPx(dp: Int): Int {
        val r = context!!.resources
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
            )
        )
    }
}