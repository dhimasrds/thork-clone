package id.thork.app.pages.about

import android.os.Bundle
import id.thork.app.R
import id.thork.app.base.BaseActivity

/**
 * Created by Raka Putra on 1/14/21
 * Jakarta, Indonesia.
 */
class AboutActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_about)
    }
}