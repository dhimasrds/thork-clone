package id.thork.app.pages.main.element

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.thork.app.pages.main.work_order_list.ActivityFragment
import id.thork.app.pages.main.work_order_list.WorkorderFragment

/**
 * Created by Dhimas Saputra on 08/01/21
 * Jakarta, Indonesia.
 */
class TabsMainAdapter(fm: FragmentManager, lifecycle: Lifecycle, private var numberOfTabs: Int) : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                // # Music Fragment
                val bundle = Bundle()
                bundle.putString("fragmentName", "Music Fragment")
                val workorderFragment = WorkorderFragment()
                workorderFragment.arguments = bundle
                return workorderFragment
            }
            1 -> {
                // # Movies Fragment
                val bundle = Bundle()
                bundle.putString("fragmentName", "Movies Fragment")
                val activityFragment = ActivityFragment()
                activityFragment.arguments = bundle
                return activityFragment
            }

            else -> return WorkorderFragment()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}