package id.thork.app.pages.rfid_create_wo_location.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.repository.AssetRepository
import id.thork.app.repository.LocationRepository

/**
 * Created by M.Reza Sulaiman on 25/05/2021
 * Jakarta, Indonesia.
 */
class RfidCreateWoLocationActivityViewModel @ViewModelInject constructor(
    private val locationRepository: LocationRepository
): LiveCoroutinesViewModel() {
}