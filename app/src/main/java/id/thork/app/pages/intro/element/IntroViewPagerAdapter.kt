package id.thork.app.pages.intro.element

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Raka Putra on 1/11/21
 * Jakarta, Indonesia.
 */
class IntroViewPagerAdapter(
    private var screens: IntArray
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(viewType, parent, false)
            return SliderViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
        override fun getItemViewType(position: Int): Int {
            return screens[position]
        }

        override fun getItemCount(): Int {
            return screens.size
        }

        inner class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
