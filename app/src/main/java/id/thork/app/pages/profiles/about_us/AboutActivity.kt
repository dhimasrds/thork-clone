package id.thork.app.pages.profiles.about_us

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import id.thork.app.R
import id.thork.app.base.BaseActivity

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class AboutActivity : BaseActivity() {
    private lateinit var webView: WebView

    var url = "https://www.this.id/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_about)
        setupToolbarWithHomeNavigation(
            getString(R.string.settings_about), navigation = false,
            filter = false, scannerIcon = false,
            notification = false, option = false,
            historyAttendanceIcon = false
        )

        webView = findViewById(R.id.webview_about)
        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun goToPreviousActivity() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.goToPreviousActivity()
        }
    }
}