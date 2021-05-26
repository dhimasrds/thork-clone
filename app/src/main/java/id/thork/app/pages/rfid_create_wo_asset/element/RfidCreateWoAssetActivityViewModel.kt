package id.thork.app.pages.rfid_create_wo_asset.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.pages.rfid_create_wo_asset.RfidCreateWoAssetActivity
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.repository.AssetRepository

/**
 * Created by M.Reza Sulaiman on 25/05/2021
 * Jakarta, Indonesia.
 */
class RfidCreateWoAssetActivityViewModel @ViewModelInject constructor(
    private val assetRepository: AssetRepository
): LiveCoroutinesViewModel() {
    private val TAG = RfidCreateWoAssetActivityViewModel::class.java.name


    private val _assetCache = MutableLiveData<AssetEntity>()
    val assetCache: LiveData<AssetEntity> get() = _assetCache


    fun checkAssetTagCode(tagCode: String) {
        val assetEntity = assetRepository.findbyTagcode(tagCode)
        assetEntity.whatIfNotNull {
            _assetCache.value = it
        }
    }



}