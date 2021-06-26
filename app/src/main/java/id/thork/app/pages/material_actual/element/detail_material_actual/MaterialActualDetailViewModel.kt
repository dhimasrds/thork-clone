package id.thork.app.pages.material_actual.element.detail_material_actual

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.pages.material_plan.element.form.MaterialPlanFormViewModel
import id.thork.app.persistence.entity.MatusetransEntity
import id.thork.app.repository.MaterialRepository

/**
 * Created by M.Reza Sulaiman on 30/05/2021
 * Jakarta, Indonesia.
 */
class MaterialActualDetailViewModel @ViewModelInject constructor(
    private val context: Context,
    private val materialRepository: MaterialRepository
) : LiveCoroutinesViewModel() {

    private val TAG = MaterialPlanFormViewModel::class.java.name

    private val _materialCache = MutableLiveData<MatusetransEntity>()
    val materialCache: LiveData<MatusetransEntity> get() = _materialCache

    private val _result = MutableLiveData<Int>()
    val result: LiveData<Int> get() = _result

    fun saveMaterialCache(matusetransEntity: MatusetransEntity, itemqty: String) {
        matusetransEntity.itemqty = itemqty.toInt()
        materialRepository.saveMaterialActual(matusetransEntity)
        _result.value = BaseParam.APP_TRUE
    }

    fun checkResultMaterial(itemnum: String, workorderid: String) {
        val materialCache = materialRepository.getMaterialActualByWoid(workorderid, itemnum)
        materialCache.whatIfNotNull {
            _materialCache.value = it
        }
    }

}