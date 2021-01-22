package id.thork.app.pages

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import id.thork.app.R
import id.thork.app.base.BaseParam

/**
 * Created by M.Reza Sulaiman on 21/01/21
 * Jepara, Indonesia.
 */
class CustomInfoWindowForGoogleMap(context: Context) : GoogleMap.InfoWindowAdapter {

    @SuppressLint("InflateParams")
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.custom_info_window, null)
    private var tvTitle: TextView? = null
    private var tvSnippet: TextView? = null
    private var tvPrefix: TextView? = null

    private fun rendowWindowText(marker: Marker, view: View) {
        tvPrefix = view.findViewById(R.id.prefixwo)
        tvTitle = view.findViewById(R.id.title)
        tvSnippet = view.findViewById(R.id.snippet)

        tvPrefix!!.text = BaseParam.APP_WONUM
        tvTitle!!.text = marker.title
        tvSnippet!!.text = BaseParam.APP_DETAIL

    }

    override fun getInfoContents(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }
}