package id.thork.app.pages.find_asset_location.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.MultiAssetEntity
import id.thork.app.repository.AssetRepository
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 24/05/21
 * Jakarta, Indonesia.
 */
class FindAssetViewModel @ViewModelInject constructor(
    private val assetRepository: AssetRepository
)
    :LiveCoroutinesViewModel(){

    private val _getFindAssetList = MutableLiveData<List<AssetEntity>>()
    val getFindAssetList: LiveData<List<AssetEntity>> get() = _getFindAssetList

        fun findAllAsset(){
          val findAssetList = assetRepository.findAllAsset()
            Timber.d("findAllAsset :%s",findAssetList!!.size)
            _getFindAssetList.value = findAssetList
        }
}