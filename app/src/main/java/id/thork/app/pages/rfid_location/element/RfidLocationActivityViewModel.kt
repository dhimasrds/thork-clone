package id.thork.app.pages.rfid_location.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.repository.LocationRepository

/**
 * Created by M.Reza Sulaiman on 19/05/2021
 * Jakarta, Indonesia.
 */
class RfidLocationActivityViewModel @ViewModelInject constructor(
    private val locationRepository: LocationRepository
) : LiveCoroutinesViewModel() {


}