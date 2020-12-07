package id.thork.app.binding

import android.view.View
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.skydoves.whatif.whatIfNotNull

object ViewBinding {

    @JvmStatic
    @BindingAdapter("toast")
    fun binToast(view: View, text: String) {
        text.whatIfNotNull {
            Toast.makeText(view.context, it, Toast.LENGTH_LONG).show()
        }
    }
}