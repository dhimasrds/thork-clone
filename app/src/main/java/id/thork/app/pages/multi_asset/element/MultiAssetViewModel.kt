package id.thork.app.pages.multi_asset.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.MultiAssetEntity
import id.thork.app.repository.MultiAssetRepository
import timber.log.Timber

class MultiAssetViewModel @ViewModelInject constructor(
    private val multiAssetRepository: MultiAssetRepository
) : LiveCoroutinesViewModel() {
    private val TAG = MultiAssetViewModel::class.java.name

    private val _getMultiAssetList = MutableLiveData<List<MultiAssetEntity>>()
    val getMultiAssetList: LiveData<List<MultiAssetEntity>> get() = _getMultiAssetList

    private val _getMultiAsset = MutableLiveData<MultiAssetEntity>()
    val getMultiAsset: LiveData<MultiAssetEntity> get() = _getMultiAsset

    private val _result = MutableLiveData<Int>()
    val result: LiveData<Int> get() = _result


    fun getMultiAssetByAssetNum(assetnum: String, wonum: String) {
        Timber.d("getMultiAssetByAssetNum assetnum:%s", assetnum)
        val multiasset = multiAssetRepository.getMultiAssetByAssetNumAndParent(assetnum, wonum)
        Timber.d("getMultiAssetByAssetNum :%s", multiasset?.assetNum)
        _getMultiAsset.value = multiasset
    }

    fun getMultiAssetByParent(wonum: String) {
        val multiAssetList = multiAssetRepository.getListMultiAssetByParent(wonum)
        _getMultiAssetList.value = multiAssetList
    }

    fun updateMultiAsset(assetnum: String, parent: String, isScan: Int, scantype: String) {
        Timber.tag(TAG).d("updateMultiAsset() %s", scantype)
        multiAssetRepository.updateMultiAsset(assetnum, parent, isScan, scantype)
        _result.value = BaseParam.APP_TRUE
    }

    fun checkingMultiAsset(assetnum: String, parent: String, isScan: Int, scantype: String) {
        val multiAsset = multiAssetRepository.getMultiAssetByAssetNumAndParent(assetnum, parent)
        multiAsset.whatIfNotNull(
            whatIf = {
                updateMultiAsset(assetnum, parent, isScan, scantype)
            },
            whatIfNot = {
                _result.value = BaseParam.APP_FALSE
            }
        )
    }

}