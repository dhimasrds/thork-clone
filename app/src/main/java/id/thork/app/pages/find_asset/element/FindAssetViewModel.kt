package id.thork.app.pages.find_asset.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.repository.AssetRepository

/**
 * Created by Dhimas Saputra on 24/05/21
 * Jakarta, Indonesia.
 */
class FindAssetViewModel @ViewModelInject constructor(
    private val assetRepository: AssetRepository
)
    :LiveCoroutinesViewModel(){

        fun findAllAsset(){
            assetRepository.findAllAsset()
        }
}