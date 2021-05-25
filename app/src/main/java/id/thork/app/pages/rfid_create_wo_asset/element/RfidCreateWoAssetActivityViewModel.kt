package id.thork.app.pages.rfid_create_wo_asset.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.repository.AssetRepository

/**
 * Created by M.Reza Sulaiman on 25/05/2021
 * Jakarta, Indonesia.
 */
class RfidCreateWoAssetActivityViewModel @ViewModelInject constructor(
    private val assetRepository: AssetRepository
): LiveCoroutinesViewModel() {
}