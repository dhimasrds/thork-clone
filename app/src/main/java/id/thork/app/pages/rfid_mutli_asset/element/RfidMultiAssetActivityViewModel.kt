package id.thork.app.pages.rfid_mutli_asset.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.MultiAssetEntity
import id.thork.app.repository.MultiAssetRepository

/**
 * Created by M.Reza Sulaiman on 21/05/2021
 * Jakarta, Indonesia.
 */
class RfidMultiAssetActivityViewModel  @ViewModelInject constructor(
    val multiAssetRepository: MultiAssetRepository
): LiveCoroutinesViewModel() {

    private val RFID_MINIMUM_MATCH = 90
    private val _multiAssetEntity = MutableLiveData<MultiAssetEntity>()
    private val _result = MutableLiveData<Int>()
    private val _percentageResult = MutableLiveData<String>()

    val multiAssetEntity: LiveData<MultiAssetEntity> get() = _multiAssetEntity
    val result: LiveData<Int> get() =  _result
    val percentageResult : MutableLiveData<String> get() = _percentageResult

    fun initMultiAsset(assetNum: String, wonum: String) {
        val multiAsset = multiAssetRepository.getMultiAssetByAssetNumAndParent(assetNum, wonum)
        _multiAssetEntity.value = multiAsset
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