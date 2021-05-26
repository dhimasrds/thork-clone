package id.thork.app.pages.rfid_create_wo_location.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.pages.rfid_create_wo_asset.element.RfidCreateWoAssetActivityViewModel
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.LocationEntity
import id.thork.app.repository.AssetRepository
import id.thork.app.repository.LocationRepository

/**
 * Created by M.Reza Sulaiman on 25/05/2021
 * Jakarta, Indonesia.
 */
class RfidCreateWoLocationActivityViewModel @ViewModelInject constructor(
    private val locationRepository: LocationRepository
): LiveCoroutinesViewModel() {
    private val TAG = RfidCreateWoLocationActivityViewModel::class.java.name

    private val _locationCache = MutableLiveData<LocationEntity>()
    val locationCache: LiveData<LocationEntity> get() = _locationCache

    fun checkLocationTagcode(tagcode : String) {
        val locationEntity =  locationRepository.findByTagcode(tagcode)
        locationEntity.whatIfNotNull {
            _locationCache.value = it
        }
    }

}