package id.thork.app.pages.multi_asset.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.MultiAssetEntity
import id.thork.app.repository.MultiAssetRepository
import timber.log.Timber

class MultiAssetViewModel @ViewModelInject constructor(
    private val multiAssetRepository: MultiAssetRepository
) : LiveCoroutinesViewModel(){


    private val _getMultiAssetList = MutableLiveData<List<MultiAssetEntity>>()
    val getMultiAssetList: LiveData<List<MultiAssetEntity>> get() = _getMultiAssetList


    fun getAllMultiAsset(){
        val multiAssetList = multiAssetRepository.getAllMultiAsset()
        Timber.d("setMultiAssetList :%s", multiAssetList.size)
        _getMultiAssetList.value = multiAssetList
    }
}