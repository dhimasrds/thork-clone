package id.thork.app.pages.material_actual.element.form

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.pages.material_plan.element.form.MaterialPlanFormViewModel
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.MatusetransEntity
import id.thork.app.repository.MaterialRepository

/**
 * Created by Dhimas Saputra on 29/05/21
 * Jakarta, Indonesia.
 */
class MaterialActualFormViewModel @ViewModelInject constructor(
    private val context: Context,
    private val materialRepository: MaterialRepository
) : LiveCoroutinesViewModel() {
    private val TAG = MaterialPlanFormViewModel::class.java.name

    private val _materialCache = MutableLiveData<MaterialEntity>()
    val materialCache: LiveData<MaterialEntity> get() = _materialCache

    private val _result = MutableLiveData<Int>()
    val result: LiveData<Int> get() = _result

    fun saveMaterialCache(materialEntity: MaterialEntity, workorderid: String, itemqty: String) {
        val matusetransEntity = MatusetransEntity()
        matusetransEntity.itemNum = materialEntity.itemNum
        matusetransEntity.description = materialEntity.description
        matusetransEntity.itemType = materialEntity.itemType
        matusetransEntity.workorderId = workorderid
        matusetransEntity.storeroom = materialEntity.storeroom
        matusetransEntity.itemqty = itemqty.toInt()
        matusetransEntity.syncUpdate = BaseParam.APP_TRUE
        materialRepository.saveMaterialActual(matusetransEntity)
        _result.value = BaseParam.APP_TRUE
    }

    fun checkResultMaterial(itemnum: String) {
        val materialCache = materialRepository.getListByItemnum(itemnum)
        materialCache.whatIfNotNull {
            _materialCache.value = it
        }
    }

}