package id.thork.app.pages.about

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import id.thork.app.R
import id.thork.app.base.BaseActivity

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class AboutActivity: BaseActivity() {
    private lateinit var webView: WebView

    var url = "https://www.this.id/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_about)
        webView = findViewById(R.id.webview_about)
        webView.setWebViewClient(WebViewClient())
        webView.loadUrl(url)
        val webSettings = webView.getSettings()
        webSettings.javaScriptEnabled = true
        val toolbar = findViewById<Toolbar>(R.id.wms_toolbar)
        val mTitle = toolbar.findViewById<TextView>(R.id.toolbar_title)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        mTitle.setText(R.string.action_settings)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_toolbar))
        toolbar.setNavigationOnClickListener { view: View? -> finish() }
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }
}