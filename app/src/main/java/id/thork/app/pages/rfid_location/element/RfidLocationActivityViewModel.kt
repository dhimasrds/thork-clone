package id.thork.app.pages.rfid_location.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.LocationEntity
import id.thork.app.repository.LocationRepository

/**
 * Created by M.Reza Sulaiman on 19/05/2021
 * Jakarta, Indonesia.
 */
class RfidLocationActivityViewModel @ViewModelInject constructor(
    private val locationRepository: LocationRepository
) : LiveCoroutinesViewModel() {

    private val RFID_MINIMUM_MATCH = 90

    private val _locationEntity = MutableLiveData<LocationEntity>()
    private val _result = MutableLiveData<Int>()
    private val _percentageResult = MutableLiveData<String>()

    val locationEntity: LiveData<LocationEntity> get() = _locationEntity
    val result: LiveData<Int> get() =  _result
    val percentageResult : MutableLiveData<String> get() = _percentageResult

    fun initLocation(location: String) {
        val location = locationRepository.findByLocation(location)
        _locationEntity.value = location
    }

    fun showAssetRfidResult(distance: Int) {
        if(distance >= RFID_MINIMUM_MATCH) {
            _percentageResult.value = distance.toString()
            _result.value = BaseParam.APP_TRUE
        } else {
            _result.value = BaseParam.APP_FALSE
        }

    }



}