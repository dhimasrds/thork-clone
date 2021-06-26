package id.thork.app.pages.rfid_create_wo_material.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.repository.MaterialRepository
import timber.log.Timber

/**
 * Created by M.Reza Sulaiman on 29/05/2021
 * Jakarta, Indonesia.
 */
class RfidMaterialActivityViewModel @ViewModelInject constructor(
    private val materialRepository: MaterialRepository
): LiveCoroutinesViewModel() {
    private val TAG = RfidMaterialActivityViewModel::class.java.name


    private val _materialCache = MutableLiveData<MaterialEntity>()
    val materialCache: LiveData<MaterialEntity> get() = _materialCache

    fun checkMaterialTagCode(tagCode: String) {
        val assetEntity = materialRepository.getListByTagcode(tagCode)
        Timber.tag(TAG).d("checkAssetTagCode() assetnum %s", assetEntity)
        assetEntity.whatIfNotNull {
            _materialCache.postValue(it)
        }
    }

}
