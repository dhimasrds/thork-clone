package id.thork.app.pages

import android.content.Context
import com.scottyab.rootbeer.RootBeer

/**
 * Created by Dhimas Saputra on 30/11/20
 * Jakarta, Indonesia.
 */

class RootBeerUtils(context: Context?) : RootBeer(context) {
    val isRootedWithoutTestKey: Boolean
        get() = (detectRootManagementApps() || detectPotentiallyDangerousApps()
                || checkForRWPaths() || checkForBinary("busybox")
                || checkSuExists() || checkForRootNative() || checkForMagiskBinary())
}