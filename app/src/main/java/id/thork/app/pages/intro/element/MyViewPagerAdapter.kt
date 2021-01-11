package id.thork.app.pages.intro.element

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import id.thork.app.base.BaseApplication

/**
 * Created by Raka Putra on 1/11/21
 * Jakarta, Indonesia.
 */
class MyViewPagerAdapter (
    private var screens: IntArray
): PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater = LayoutInflater.from(BaseApplication.context).context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(screens.get(position), container, false)
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return screens.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val v = `object` as View
        container.removeView(v)
    }

    override fun isViewFromObject(v: View, `object`: Any): Boolean {
        return v === `object`
    }
}