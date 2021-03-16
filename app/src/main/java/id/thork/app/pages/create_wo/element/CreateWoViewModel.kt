package id.thork.app.pages.create_wo.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.pages.settings.SettingsActivity
import id.thork.app.repository.LoginRepository
import id.thork.app.utils.StringUtils
import timber.log.Timber

/**
 * Created by Raka Putra on 3/15/21
 * Jakarta, Indonesia.
 */
class CreateWoViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {
    private val TAG = SettingsActivity::class.java.name

    private var tempWonum: String? = null

    fun getTempWonum(): String? {
        if (tempWonum == null) {
            tempWonum = "WO-" + StringUtils.generateUUID()
            Timber.d("getTempWonum %s", tempWonum)
        }
        return tempWonum
    }

    fun estDuration(jam: Int, menit: Int): Double {
        val hasil: Double
        val hasilDetik: Int = jam * 3600 + menit * 60
        val hasilDetikDouble = hasilDetik.toDouble()
        hasil = hasilDetikDouble / 3600
        Timber.d("hasil detail : %s", hasilDetikDouble)
        Timber.d("hasil : %s", hasil)
        return hasil
    }
}