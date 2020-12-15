package id.thork.app.extensions

import android.os.Parcelable
import androidx.activity.ComponentActivity

fun <T: Parcelable> ComponentActivity.argument(key: String): Lazy<T> {
    return lazy { requireNotNull(intent.getParcelableExtra<T>(key)) }
}