package id.thork.app.pages.rfid_asset.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.repository.AssetRepository

/**
 * Created by M.Reza Sulaiman on 18/05/2021
 * Jakarta, Indonesia.
 */
class RfidAssetActivityViewModel @ViewModelInject constructor(
    private val assetRepository: AssetRepository
) : LiveCoroutinesViewModel() {

    private val RFID_MINIMUM_MATCH = 90

    private val _assetEntity = MutableLiveData<AssetEntity>()
    private val _result = MutableLiveData<Int>()
    private val _percentageResult = MutableLiveData<String>()

    val assetEntity: LiveData<AssetEntity> get() = _assetEntity
    val result: LiveData<Int> get() =  _result
    val percentageResult : MutableLiveData<String> get() = _percentageResult

    fun initAsset(assetnum: String) {
        val asset = assetRepository.findbyAssetnum(assetnum)
        _assetEntity.value = asset
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